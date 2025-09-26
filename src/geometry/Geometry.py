import numpy as np
import inspect


class NearestPointCalculator:
    def __init__(self, position: np.ndarray, direction: np.ndarray, tolerance:float=1e-10):
        xp = inspect.getmodule(type(position))
        self.intersection = NearestPointCalculator.intersect(position, direction, tolerance=tolerance)
        self.average_pos = xp.mean(position, axis=np.arange(0, position.ndim - 1), keepdims=True)
        self.average_dir = xp.mean(direction, axis=np.arange(0, direction.ndim - 1), keepdims=True)
        self.uniformness = xp.sum(self.average_dir ** 2, axis=-1) / (np.prod(self.average_dir.shape[:-1]) ** 2)

    @staticmethod
    def intersect(
            bases: np.ndarray,
            vecs: np.ndarray,
            tolerance:float=1e-10):
        """
        Compute the intersection point of a set of lines in an arbitrary-dimensional space.

        Each line is defined by a base point and a direction vector.
        The function calculates the intersection point by projecting the base points onto
        the null spaces of the direction vectors, then solving the resulting linear system.

        Parameters:
        -----------
        bases : numpy.ndarray
            An array of shape (n, d) where each row represents the base point of a line in d-dimensional space.
        vecs : numpy.ndarray
            An array of shape (n, d) where each row represents the direction vector of a line in d-dimensional space.

        Returns:
        --------
        numpy.ndarray
            A 1D array of shape (d,) representing the intersection point of the lines.
            If the input data is invalid, or if the lines do not intersect uniquely,
            the function returns an array filled with NaN.
        """
        xp = inspect.getmodule(type(bases))
        bases = xp.asarray(bases)
        vecs = xp.asarray(vecs)
        ray_ok = ~xp.any(xp.isnan(bases) | xp.isnan(vecs), axis=-1)
        if len(bases.shape) == 2:
            bases = bases[ray_ok]
            vecs = vecs[ray_ok]
            n = bases.shape[0]
            d = bases.shape[1]
            if n < 2:
                return xp.full(d, fill_value=xp.nan)

            vecs = vecs / xp.linalg.norm(vecs, axis=1, keepdims=True)  # Normalize vecs

            M_sum = xp.eye(vecs.shape[1]) * n - xp.einsum('ki,kj->ij', vecs, vecs)  # Shape: (d, d)
            # Check rank
            #if xp.linalg.det(M_sum) < 1e-10:
            #    return xp.full(d, fill_value=xp.nan)
            eigval = xp.linalg.eigvalsh(M_sum)
            if eigval[0] < tolerance:
                return xp.full(d, fill_value=xp.nan)

            Mbase_sum = xp.sum(bases, axis=0) - xp.dot(xp.einsum('ij,ij->i', vecs, bases), vecs)  # Shape: (d)
            return xp.linalg.solve(M_sum, Mbase_sum)
        else:
            bases = xp.where(ray_ok[..., None], bases, 0)
            vecs = xp.where(ray_ok[..., None], vecs, 0)
            vecs_norm = xp.linalg.norm(vecs, axis=-1, keepdims=True)
            vecs = xp.divide(vecs, vecs_norm, where=vecs_norm > 0)

            valid_counts = xp.count_nonzero(ray_ok, axis=-1, keepdims=True)
            identity = xp.eye(vecs.shape[-1])[None, :, :]  # Shape (1, d, d)
            M_sum = valid_counts[:, None] * identity - xp.einsum('mij,mik->mjk', vecs, vecs)

            #non_singular_mask = ~(xp.linalg.det(M_sum) < 1e-10)

            eigvals = xp.linalg.eigvalsh(M_sum)  # Shape: (batch, d)
            non_singular_mask = eigvals[..., 0] > tolerance  # Smallest eigenvalue > threshold

            proj = xp.einsum('mij,mij->mi', vecs, bases)[..., None] * vecs
            Mbase_sum = xp.sum(bases - proj, axis=1)

            intersections = xp.full((M_sum.shape[0], vecs.shape[-1]), xp.nan)
            intersections[non_singular_mask] = xp.linalg.solve(M_sum[non_singular_mask],
                                                               Mbase_sum[non_singular_mask])
            return intersections

class Geometry:
    @staticmethod
    def getOrthorgonalZMatrixOld(in_vec):
        dirlength = np.linalg.norm(in_vec)
        qx, qy, qz = np.square(in_vec)

        lqx = qy + qz
        lqy = qx + qz
        lqz = qx + qy

        if lqx >= lqy and lqx >= lqz:
            n0 = np.asarray((0, -in_vec[2], in_vec[1]))
        elif lqy >= lqx and lqy >= lqz:
            n0 = np.asarray((-in_vec[2], 0, in_vec[0]))
        else:
            n0 = np.asarray((-in_vec[1], in_vec[0], 0))

        n0 = n0 * (dirlength / np.linalg.norm(n0))
        n1 = np.cross(in_vec, n0)
        n1 = n1 * (dirlength / np.linalg.norm(n1))
        return np.asarray((n0, n1, in_vec))

    @staticmethod
    def getOrthorgonalZMatrix(in_vec):
        dirlength = np.linalg.norm(in_vec)
        q = np.abs(in_vec)

        if q[0] >= q[1] and q[0] >= q[2]:
            n0 = np.asarray((-in_vec[2], 0, in_vec[0]))
        elif q[1] >= q[0] and q[1] >= q[2]:
            n0 = np.asarray((in_vec[1], -in_vec[0], 0))
        else: # q[2] >= q[0] and q[2] >= q[1]:
            n0 = np.asarray((0, in_vec[2], -in_vec[1]))
        n0 = n0 * (dirlength / np.linalg.norm(n0))
        n1 = np.cross(in_vec, n0)
        n1 = n1 * (dirlength / np.linalg.norm(n1))
        return np.stack((n0, n1, in_vec),axis=-1)

    @staticmethod
    def getOrthogonalVector(in_vec):
        dirlength = np.linalg.norm(in_vec)
        q = np.abs(in_vec)

        if q[0] >= q[1] and q[0] >= q[2]:
            n0 = np.asarray((-in_vec[2], 0, in_vec[0]))
        elif q[1] >= q[0] and q[1] >= q[2]:
            n0 = np.asarray((in_vec[1], -in_vec[0], 0))
        else: # q[2] >= q[0] and q[2] >= q[1]:
            n0 = np.asarray((0, in_vec[2], -in_vec[1]))
        return n0 * (dirlength / np.linalg.norm(n0))

    @staticmethod
    def calcTriangleMeshVertexFaceNormals(vertices, faces):
        face_vertices = vertices[faces]
        face_normals = np.cross(face_vertices[:, 1] - face_vertices[:, 0], face_vertices[:, 2] - face_vertices[:, 0])
        face_normals /= np.linalg.norm(face_normals, axis=-1, keepdims=True)

        vertex_normals = np.zeros_like(vertices, dtype=float)
        for face_index in range(0, len(faces)):
            vertex_normals[faces[face_index]] += face_normals[face_index]

        vertex_normals /= np.linalg.norm(vertex_normals, axis=-1, keepdims=True)
        return vertex_normals, face_normals