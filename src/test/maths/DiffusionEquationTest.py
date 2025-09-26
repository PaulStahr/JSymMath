import numpy as np
import unittest
from jsymmath.maths.DiffusionEquation import solve_diffusion_equation

class TestDiffusionEquation(unittest.TestCase):
    def test_diffusion_equation_1d(self):
        size = 10
        xp = np
        data = xp.zeros(size, dtype=float)
        data[1:-1] = 1.0
        is_given = xp.zeros(size, dtype=bool)
        is_given[0] = True
        is_given[-1] = True

        data = solve_diffusion_equation(data, is_given, xp=xp)

        i = np.arange(size)
        desired = 4.5 ** 2 * 0.5 - 0.5 * (i - 4.5) ** 2
        np.testing.assert_allclose(actual=data, desired=desired, atol=0.01)

    def test_diffusion_equation_homogeneous_1d(self):
        for dim in (1, 2, 3):
            size = [10] + [3] * (dim - 1)
            xp = np
            data = xp.zeros(size, dtype=float)
            is_given = xp.zeros(size, dtype=bool)
            is_given[0] = True
            is_given[-1] = True
            data[0] = 0.0
            data[-1] = 1.0

            data = solve_diffusion_equation(data, is_given, xp=xp)

            data = data[tuple([slice(None)] + [0]*(dim-1))]
            desired = np.linspace(0, 1, len(data))
            np.testing.assert_allclose(actual=data, desired=desired, atol=1e-5)


    def test_with_tails_1d(self):
        size = 5
        xp = np
        data = xp.zeros(size, dtype=float)
        data[1] = -1.0
        data[3] = 1.0
        is_given = data != 0

        data = solve_diffusion_equation(data, is_given, xp=xp)
        desired = xp.array([-1.0, -1.0,  0.0, 1.0, 1.0])
        np.testing.assert_allclose(actual=data, desired=desired, atol=1e-5)


    def test_diffusion_equation_2d(self):
        for axis in range(2):
            size = (10, 10)
            xp = np
            sl = [slice(None)] * 2
            sl[axis] = slice(1, -1)

            data = xp.zeros(size, dtype=float)
            is_given = xp.ones(size, dtype=bool)
            data[tuple(sl)] = 1.0
            is_given[tuple(sl)] = False

            data = solve_diffusion_equation(data, is_given, xp=xp)
            i, j = np.indices(size)
            if axis == 0:
                desired = 4.5 ** 2 * 0.5 - 0.5 * (i - 4.5) ** 2
            else:
                desired = 4.5 ** 2 * 0.5 - 0.5 * (j - 4.5) ** 2
            np.testing.assert_allclose(actual=data, desired=desired, atol=1e-5)


    def test_diffusion_equation_2d_cupy(self):
        import cupy as cp
        xp = cp
        for axis in range(2):
            size = (10, 10)
            sl = [slice(None)] * 2
            sl[axis] = slice(1, -1)

            data = xp.zeros(size, dtype=float)
            is_given = xp.ones(size, dtype=bool)
            data[tuple(sl)] = 1.0
            is_given[tuple(sl)] = False

            data = solve_diffusion_equation(data, is_given, xp=xp)
            i, j = np.indices(size)
            if axis == 0:
                desired = 4.5 ** 2 * 0.5 - 0.5 * (i - 4.5) ** 2
            else:
                desired = 4.5 ** 2 * 0.5 - 0.5 * (j - 4.5) ** 2
            np.testing.assert_allclose(actual=data.get(), desired=desired, atol=1e-4)
