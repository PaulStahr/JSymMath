import inspect

class AffineMatrix:
    def __init__(self, mat):
        if isinstance(mat, AffineMatrix):
            self.mat = mat.mat.copy()
        else:
            self.mat = mat.copy()
        self.xp = inspect.getmodule(type(self.mat))


    def __mul__(self, other):
        if isinstance(other, AffineMatrix):
            return AffineMatrix(self.mat @ other.mat)
        elif isinstance(other, (list, tuple)):
            return self.mat @ other
        else:
            raise TypeError(f"Unsupported type for multiplication: {type(other)}")

    def as_matrix(self):
        return self.mat


    def apply(self, points):
        return (points @ self.xp.swapaxes(self.mat[..., 0:3, 0:3], -1, -2)) + self.mat[..., 0:3, 3]


    def inv(self):
        return AffineMatrix(self.xp.linalg.inv(self.mat))