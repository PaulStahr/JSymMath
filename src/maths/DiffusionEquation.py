import numpy as np


def solve_diffusion_equation(equality_result:np.ndarray, is_given_mask:np.ndarray, xp=np):
    assert is_given_mask.dtype == bool, "is_given_mask must be a boolean array"
    ndim = equality_result.ndim
    shape = equality_result.shape
    not_given_mask = ~is_given_mask

    not_given2domain = xp.nonzero(not_given_mask)
    not_given_neighbor2domain = xp.asarray(not_given2domain, dtype=int)
    not_given_count = len(not_given2domain[0])
    domain2not_given = xp.full(fill_value=-1, shape=shape, dtype=int)
    domain2not_given[not_given2domain] = xp.arange(not_given_count, dtype=int)

    b = equality_result[not_given2domain].astype(float).flatten()

    row = []
    col = []
    data = []
    diagonal_data = xp.zeros(shape=not_given_count, dtype=float)
    not_given2domain = xp.asarray(not_given2domain)

    for d in range(ndim):
        for dir in (-1,1):
            not_given_neighbor2domain[d] += dir
            valid = xp.nonzero((0 <= not_given_neighbor2domain[d]) & (not_given_neighbor2domain[d] < shape[d]))
            not_given_valid2domain = tuple(not_given2domain[:,*valid])
            not_given_neighbor_valid2domain = tuple(not_given_neighbor2domain[:,*valid])

            src = domain2not_given[not_given_valid2domain]
            dst = domain2not_given[not_given_neighbor_valid2domain]

            neighbor_vals = equality_result[not_given_neighbor_valid2domain]

            known_mask = dst == -1
            unknown_indices = xp.nonzero(~known_mask)
            known_indices = xp.nonzero(known_mask)

            # Contribution from known neighbor to RHS
            b[known_indices] += neighbor_vals[known_indices]

            # Contribution from unknown neighbor to matrix
            row.append(src[unknown_indices])
            col.append(dst[unknown_indices])
            data.append(xp.full(shape=len(unknown_indices[0]), fill_value=-1, dtype=float))
            diagonal_data[src] += 1
            not_given_neighbor2domain[d] -= dir

    # Diagonal indices: (i, i)
    diag_indices = xp.arange(not_given_count)

    # Append diagonal entries to COO matrix components
    row.append(diag_indices)
    col.append(diag_indices)
    data.append(diagonal_data)

    row = xp.concatenate(row, dtype=int)
    col = xp.concatenate(col, dtype=int)
    data = xp.concatenate(data, dtype=float)

    if xp == np:
        from scipy.sparse import coo_matrix
        from scipy.sparse.linalg import spsolve
        A = coo_matrix((data, (row, col)), shape=(not_given_count, not_given_count)).tocsr()
        x_result = spsolve(A, b)
    else:
        from cupyx.scipy.sparse import coo_matrix
        from cupyx.scipy.sparse.linalg import spsolve
        A = coo_matrix((data, (row, col)), shape=(not_given_count, not_given_count)).tocsr()
        x_result = spsolve(A, b)

    # Insert results into the original equality_result array
    equality_result = equality_result.copy()
    equality_result[tuple(not_given2domain)] = x_result.reshape(-1)
    return equality_result