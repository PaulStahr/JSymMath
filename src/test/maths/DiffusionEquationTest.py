import numpy as np
import pytest
from jsymmath.maths.DiffusionEquation import solve_diffusion_equation

def test_diffusion_equation_1d():
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

def test_diffusion_equation_2d():
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
        np.testing.assert_allclose(actual=data, desired=desired, atol=0.01)


def test_diffusion_equation_2d_cupy():
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
        np.testing.assert_allclose(actual=data.get(), desired=desired, atol=0.01)
