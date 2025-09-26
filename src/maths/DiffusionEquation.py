import gc
import logging

logger = logging.getLogger(__name__)

import numpy as np
import scipy.sparse as sp
from scipy.sparse.linalg import LinearOperator
from concurrent.futures import ThreadPoolExecutor
import threading

class ParallelRowPartitionedMatrix(LinearOperator):
    def __init__(self, arg1, shape=None, dtype=np.float32, n_jobs=4, n_blocks=None):
        self._dtype = np.dtype(dtype)
        self.n_jobs = n_jobs
        self.n_blocks = n_blocks or n_jobs
        self.dtype = self._dtype

        # Build COO matrix
        if isinstance(arg1, sp.coo_matrix):
            coo = arg1
            shape = coo.shape
        else:
            coo = sp.coo_matrix(arg1, shape=shape, dtype=self._dtype)


        # Partition rows into contiguous ranges
        row_splits = np.array_split(np.arange(shape[0]), self.n_blocks)
        self.mats = []
        self._executor = ThreadPoolExecutor(max_workers=self.n_jobs)

        def create_submatrix(start, stop):
            mask = (coo.row >= start) & (coo.row < stop)
            return sp.coo_matrix(
                (coo.data[mask], (coo.row[mask] - start, coo.col[mask])),
                shape=(stop - start, shape[1]),
                dtype=self._dtype,
            ).tocsr()

        if self._executor is not None:
            futures = [self._executor.submit(create_submatrix, rows[0], rows[-1] + 1) for rows in row_splits]
            results = [f.result() for f in futures]
        else:
            results = [create_submatrix(rows[0], rows[-1] + 1) for rows in row_splits]
        self.mats = [((rows[0], rows[-1] + 1), mat) for rows, mat in zip(row_splits, results)]
        super().__init__(dtype=self._dtype, shape=shape)

    def _matvec(self, x):
        """Parallel y = A @ x"""

        def worker(mat) -> np.ndarray:
            return mat @ x
        if self._executor is not None:
            futures = [self._executor.submit(worker, mat) for (_, mat) in self.mats]
            results = [f.result() for f in futures]
        else:
            with ThreadPoolExecutor(max_workers=self.n_jobs) as ex:
                futures = [ex.submit(worker, mat) for (_, mat) in self.mats]
                results = [f.result() for f in futures]
        return np.concatenate(results)

    def close(self):
        self._executor.shutdown(wait=True)


def solve_diffusion_equation(
        temperature:np.ndarray,
        is_given_mask:np.ndarray,
        conductivity_profile:np.ndarray=None,
        neighbor_distance:int=1,
        max_workers:int=4,
        xp=np):
    assert is_given_mask.dtype == bool, "is_given_mask must be a boolean array"
    ndim = temperature.ndim
    shape = temperature.shape

    dtype = xp.float32
    not_given2domain = xp.nonzero(~is_given_mask)
    not_given_neighbor2domain_array = xp.asarray(not_given2domain, dtype=np.int32)
    not_given_count = len(not_given2domain[0])
    assert not_given_count < 2**31, "Too many not given points, cannot handle more than 2 billion unknowns"
    domain2not_given = xp.full(fill_value=-1, shape=shape, dtype=np.int32)
    domain2not_given[not_given2domain] = xp.arange(not_given_count, dtype=np.int32)

    b = temperature[not_given2domain].astype(dtype, copy=True).ravel()

    row = []
    col = []
    data = []
    diagonal_data = xp.zeros(shape=not_given_count, dtype=dtype)
    not_given2domain_array = xp.asarray(not_given2domain)

    #create neighbor offsets with distance dist
    cuberadius = int(np.ceil(np.sqrt(neighbor_distance)))
    grid = xp.mgrid[tuple(slice(-cuberadius,cuberadius + 1) for _ in range(ndim))].reshape(ndim, -1).astype(np.int32)
    distances = xp.sum(xp.square(grid), axis=0)
    neighbor_offsets = grid[:, (distances > 0) & (distances <= neighbor_distance)]
    logger.log(logging.INFO, F"Using {neighbor_offsets.shape[1]} neighbors with distance <= {neighbor_distance}") #TODO set to debug


    n_offsets = neighbor_offsets.shape[1]

    # One event per offset, to enforce order
    events = [threading.Event() for _ in range(n_offsets + 1)]
    events[0].set()  # offset[0] can start accumulation immediately

    def process_offset(idx, offset:np.ndarray):
        not_given_neighbor_valid2domain = not_given_neighbor2domain_array + offset[:, xp.newaxis]
        valid = xp.nonzero(np.all((0 <= not_given_neighbor_valid2domain) &
                                  (not_given_neighbor_valid2domain < xp.asarray(shape)[:, xp.newaxis]),
                                  axis=0))
        not_given_valid2domain = tuple(not_given2domain_array[:, *valid])
        not_given_neighbor_valid2domain = tuple(not_given_neighbor_valid2domain[:, *valid])
        conductivity = 1.0 / np.linalg.norm(offset)

        if conductivity_profile is not None:
            divisor = (1 + np.abs(
                conductivity_profile[not_given_valid2domain].astype(np.float32) -
                conductivity_profile[not_given_neighbor_valid2domain].astype(np.float32)
            ))
            conductivity = conductivity / divisor

        src = domain2not_given[not_given_valid2domain]
        dst = domain2not_given[not_given_neighbor_valid2domain]
        neighbor_temperature = temperature[not_given_neighbor_valid2domain]

        dst_given_mask = dst == -1
        dst_not_given_indices = xp.nonzero(~dst_given_mask)
        neighbor_given_indices = xp.nonzero(dst_given_mask)

        def get_elements(data, indices):
            return data[indices] if isinstance(data, xp.ndarray) and data.ndim > 0 else data

        add_temperature = neighbor_temperature[neighbor_given_indices] * get_elements(conductivity, neighbor_given_indices)
        current_row_indices = src[dst_not_given_indices]
        current_col_indices = dst[dst_not_given_indices]
        current_data_values = xp.full(len(dst_not_given_indices[0]), -1, dtype=dtype) * get_elements(conductivity, dst_not_given_indices)
        current_b_indices = src[neighbor_given_indices]

        # --- synchronization barrier ---
        events[idx].wait()  # wait until all lower indices finished

        # --- single-threaded accumulation in correct order ---
        b[current_b_indices] += add_temperature
        row.append(current_row_indices)
        col.append(current_col_indices)
        data.append(current_data_values)
        diagonal_data[src] += conductivity

        # release the next offset
        events[idx + 1].set()

    # Run with a pool of at most 4 threads
    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = [
            executor.submit(process_offset, idx, offset)
            for idx, offset in enumerate(neighbor_offsets.T)
        ]
        for f in futures:
            f.result()  # wait for all to finish

    del not_given2domain_array
    del not_given_neighbor2domain_array

    # Diagonal indices: (i, i)
    diag_indices = xp.arange(not_given_count)
    row.append(diag_indices)
    col.append(diag_indices)
    del diag_indices
    data.append(diagonal_data)
    del diagonal_data

    row = xp.concatenate(row, dtype=np.int32)
    col = xp.concatenate(col, dtype=np.int32)
    data = xp.concatenate(data, dtype=dtype)
    gc.collect()

    used_mem = (row.nbytes + col.nbytes + data.nbytes + b.nbytes) / (1024**3)
    logger.log(logging.INFO, F"Solving linear system of size {not_given_count}x{not_given_count} with {len(data)} non-zero entries, used memory {used_mem} GB") #TODO set to debug
    method = "cg"
    if xp == np:
        from scipy.sparse import coo_matrix
        from scipy.sparse.linalg import spsolve, cg, gmres, LinearOperator, spilu
        #A = coo_matrix((data, (row, col)), shape=(not_given_count, not_given_count), dtype=xp.float32).tocsr()
        A = ParallelRowPartitionedMatrix( (data, (row, col)), shape=(not_given_count, not_given_count), dtype=np.float32, n_jobs=max_workers)
        del data
        del row
        del col
        gc.collect()
        info = 0
        if method == "cg":
            from threadpoolctl import threadpool_limits, threadpool_info
            with threadpool_limits(limits=8, user_api='blas'):
                x_result, info = cg(A, b, rtol=1e-6)
        elif method == "gmres":
            ilu = spilu(A)
            M = LinearOperator(A.shape, lambda x: ilu.solve(x))
            x_result, info = gmres(A, b, M=M)
        if info != 0:
            logger.log(logging.WARNING, f"Conjugate gradient solver did not converge, info={info}, falling back to spsolve")
        if method == "spsolve" or info != 0:
            x_result = spsolve(A, b)
    else:
        from cupyx.scipy.sparse import coo_matrix
        from cupyx.scipy.sparse.linalg import spsolve

        gc.collect()
        A = coo_matrix((data, (row.astype(xp.uint32), col.astype(xp.uint32))), shape=(not_given_count, not_given_count), dtype=xp.float32).tocsr()
        del data
        del row
        del col

        from cupyx.scipy.sparse.linalg import cg
        if method == "cg":
            x_result, info = cg(A, b, tol=1e-6)
            if info != 0:
                logger.log(logging.WARNING, f"Conjugate gradient solver did not converge, info={info}, falling back to spsolve")
                method = "spsolve"
        if method == "spsolve":
            x_result = spsolve(A, b)

    # Insert results into the original equality_result array
    temperature = temperature.copy()
    temperature[not_given2domain] = x_result.reshape(-1)
    return temperature