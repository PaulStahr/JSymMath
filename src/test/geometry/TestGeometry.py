import jpype
import sys

from jsymmath.geometry.Geometry import Geometry
from jpype import JPackage
import numpy as np
sys.path.append("./bin")
if not jpype.isJVMStarted():
    jpype.startJVM(classpath=["./JSymMath.jar"])
geometry_package = JPackage("geometry")

def test_getOrthorgonalZMatrix():
    rng = np.random.default_rng(42)
    for i in range(10):
        in_vec = rng.normal(size=3)
        result = Geometry.getOrthorgonalZMatrix(in_vec)

        # get same function from java machine and compare
        jp_in_vec = geometry_package.Vector3d(in_vec[0], in_vec[1], in_vec[2])
        jp_out_matrix = geometry_package.Matrix3d()
        geometry_package.Geometry.getOrthorgonalZMatrix(jp_in_vec, jp_out_matrix)

        jp_result = jp_out_matrix.toArrayD()
        jp_result = np.array(jp_result).reshape((3, 3))
        np.testing.assert_allclose(result, jp_result, rtol=1e-8)