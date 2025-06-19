import unittest
from parameterized import parameterized
import numpy as np
from jsymmath.geometry.Geometry import NearestPointCalculator



class NearestPointCalculatorTest(unittest.TestCase):

    @parameterized.expand([
        (np.asarray((0, 0, 0)),),
        (np.asarray((1, 0, 0)),),
        (np.asarray((0, 1, 0)),),
        (np.asarray((0, 0, 100)),),
    ])
    def test_nearest_point_calculator_array(self, translate:np.ndarray):
        bases = np.asarray([[0, 1, -2], [2, 1, -1]], dtype=float)
        bases += translate
        directions = np.asarray([[0, 1, 2], [0, 1, 1]], dtype=float)
        npc = NearestPointCalculator(bases, directions)
        result = np.asarray([1,2,0], dtype=float) + translate
        np.testing.assert_allclose(npc.intersection, result, rtol=1e-10, atol=1e-10)



    @parameterized.expand([
        (np.asarray((0, 0, 0)),),
        (np.asarray((1, 0, 0)),),
        (np.asarray((0, 1, 0)),),
        (np.asarray((0, 0, 100)),),
    ])
    def test_nearest_point_calculator_cross(self, translate):
        bases = np.repeat([translate], 3, axis=0)
        directions = np.identity(3, dtype=float)
        npc = NearestPointCalculator(bases, directions)
        np.testing.assert_allclose(npc.intersection, translate, rtol=1e-10, atol=1e-10)

if __name__ == "__main__":
    unittest.main()
