package test.geometry;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import geometry.Geometry;
import geometry.Vector3d;
import util.ArrayUtil;
import util.Interpolator;
import util.data.DoubleArrayList;
import util.data.IntegerArrayList;

public class GeometryTest {
    public static interface InitFunctional2d{
        public float eval(float x, float y);
    }

    public static interface InitFunctional3d{
        public float eval(float x, float y, float z);
    }

    public static final InitFunctional3d sphereGenerator = new InitFunctional3d() {
        @Override
        public float eval(float x, float y, float z)
        {
            return (float)(Math.sqrt(x * x + y * y + z * z) - 0.5);
        }
    };

    public static final InitFunctional3d identityXGenerator = new InitFunctional3d() {
        @Override
        public float eval(float x, float y, float z)
        {
            return x;
        }
    };

    public static final InitFunctional3d identityYGenerator = new InitFunctional3d() {
        @Override
        public float eval(float x, float y, float z)
        {
            return y;
        }
    };

    public static final InitFunctional3d identityZGenerator = new InitFunctional3d() {
        @Override
        public float eval(float x, float y, float z)
        {
            return z;
        }
    };

    public static final InitFunctional2d circleGenerator = new InitFunctional2d() {
        @Override
        public float eval(float x, float y)
        {
            return (float)(Math.sqrt(x * x + y * y) - 0.5);
        }
    };

    public static final InitFunctional2d identityXGenerator2d = new InitFunctional2d() {
        @Override
        public float eval(float x, float y)
        {
            return x;
        }
    };

    public static final InitFunctional2d identityYGenerator2d = new InitFunctional2d() {
        @Override
        public float eval(float x, float y)
        {
            return y;
        }
    };



    public static float eval(InitFunctional3d initF, float x, float y, float z, int width, int height, int depth)
    {
        float invWidth = 1f/width, invHeight = 1f/height, invDepth = 1f / depth;
        return initF.eval((x * 2 + 1 - width) * invWidth, (y * 2 + 1 - height) * invHeight, (z * 2 + 1 - depth) * invDepth);
    }

    public static float eval(InitFunctional2d initF, float x, float y, int width, int height)
    {
        float invWidth = 1f/width, invHeight = 1f/height;
        return initF.eval((x * 2 + 1 - width) * invWidth, (y * 2 + 1 - height) * invHeight);
    }

    public static float[] generateArray(InitFunctional3d f, int width, int height, int depth) {
        float data[] = new float[width * height * depth];
        for (int z = 0, index = 0; z < depth; ++z)
        {
            for (int y = 0; y < height; ++y)
            {
                for (int x = 0; x < width; ++x)
                {
                    data[index ++] = eval(f, x, y, z, width, height, depth);
                }
            }
        }
        return data;
    }

    public static float[] generateArray(InitFunctional2d f, int width, int height) {
        float data[] = new float[width * height];
        for (int y = 0, index = 0; y < height; ++y)
        {
            for (int x = 0; x < width; ++x)
            {
                data[index ++] = eval(f, x, y, width, height);
            }
        }
        return data;
    }


    private static interface VolumeToMeshAdapter{
        public void volumeToMesh(float data[], int width, int height, int depth, double mid, IntegerArrayList faceIndices, DoubleArrayList vertexPositions);
    }

    @Test
    public void testCollapseSmallEdges() {
        DoubleArrayList dal = new DoubleArrayList();
        IntegerArrayList ial = new IntegerArrayList();
        dal.addTuple(0, 1, 0);
        dal.addTuple(0, 0, -0.1);
        dal.addTuple(0, 0, 0.1);
        dal.addTuple(1, 0, 0);
        ial.addTuple(1,2,3);
        ial.addTuple(0,1,3);
        Geometry.collapseShortEdges(dal, ial, 0.3);
        DoubleArrayList expectedVertices = new DoubleArrayList();
        expectedVertices.addTuple(0, 1, 0);
        expectedVertices.addTuple(0, 0, 0);
        expectedVertices.addTuple(1, 0, 0);
        assertEquals(expectedVertices.size(), dal.size());
        assertEquals(dal.size(), 9);
        Vector3d v0 = new Vector3d(), v1 = new Vector3d();
        for (int v = 0; v < expectedVertices.size(); v += 3)
        {
            v0.set(dal, v);
            v1.set(expectedVertices, v);
            assertEquals("Expected " + v1 + " got " + v0, 0, v0.distanceQ(v1), 0.01);
        }
        assertEquals(ial.getI(0), 0);
        assertEquals(ial.getI(1), 1);
        assertEquals(ial.getI(2), 2);
    }

    @RunWith(Parameterized.class)
    public static class VolumeToMeshTest {
        VolumeToMeshAdapter meshAdapter;
        public VolumeToMeshTest(VolumeToMeshAdapter meshAdapter) {
            this.meshAdapter = meshAdapter;
        }

        @Parameters
        public static List<VolumeToMeshAdapter> params()
        {
            return Arrays.asList(new VolumeToMeshAdapter() {
                @Override
                public void volumeToMesh(float[] data, int width, int height, int depth, double mid,
                        IntegerArrayList faceIndices, DoubleArrayList vertexPositions) {
                    Geometry.volumeToMesh(data, width, height, depth, mid, faceIndices, vertexPositions);
                }
            }, new VolumeToMeshAdapter() {
                @Override
                public void volumeToMesh(float[] data, int width, int height, int depth, double mid,
                        IntegerArrayList faceIndices, DoubleArrayList vertexPositions) {
                    int dataI[] = new int[data.length];
                    float minMax[] = new float[2];
                    ArrayUtil.minMax(data, minMax);
                    ArrayUtil.mult(data, 0, data.length, dataI, 0, Integer.MAX_VALUE / Math.max(-minMax[0], minMax[0]));
                    Geometry.volumeToMesh(data, width, height, depth, mid, faceIndices, vertexPositions);
                }
            }, new VolumeToMeshAdapter() {
                @Override
                public void volumeToMesh(float[] data, int width, int height, int depth, double mid,
                    IntegerArrayList faceIndices, DoubleArrayList vertexPositions) {
                    DoubleArrayList dal = new DoubleArrayList();
                    for (int i = 0; i < data.length; ++i) {dal.add(data[i]);}
                    Geometry.volumeToMesh(dal, width, height, depth, mid, faceIndices, vertexPositions);
                }
            });}

        @Test
        public void testVolumeToMesh(){
            int width = 10, height = 10, depth = 10;
            float data[] = generateArray(sphereGenerator, width, height, depth);
            IntegerArrayList faceIndices = new IntegerArrayList();
            DoubleArrayList vertexPositions = new DoubleArrayList();
            meshAdapter.volumeToMesh(data, width, height, depth, 0, faceIndices, vertexPositions);
            Vector3d v = new Vector3d();
            float expectedDist = 10f/4;
            double eps = 0.05;
            for (int i = 0; i < vertexPositions.size(); i += 3)
            {
                v.set(vertexPositions, i);
                float smoothed = Interpolator.interpolatePoint(v.x, v.y, v.z, data, width, height, depth);
                assertEquals(smoothed, 0, 0.05);
                double dist = Math.sqrt(v.distanceQ(4.5, 4.5, 4.5));
                assertEquals(dist, expectedDist, eps);
            }
            Geometry.checkMesh(vertexPositions, faceIndices);
            Geometry.collapseShortEdges(vertexPositions, faceIndices, eps * 0.1);
            Geometry.checkMesh(vertexPositions, faceIndices);
            for (int i = 0; i < vertexPositions.size(); i += 3)
            {
                v.set(vertexPositions, i);
                float smoothed = Interpolator.interpolatePoint(v.x, v.y, v.z, data, width, height, depth);
                assertEquals(smoothed, 0, 0.05);
                double dist = Math.sqrt(v.distanceQ(4.5, 4.5, 4.5));
                assertEquals(dist, expectedDist, eps);
            }
        }

        @Test
        public void clipCornerTest() {
            float data[] = new float[8];
            IntegerArrayList faceIndices = new IntegerArrayList();
            DoubleArrayList vertexPositions = new DoubleArrayList();
            for (int i = 0; i < 8; ++i)
            {
                Arrays.fill(data, -1);
                data[i] = 0.000001f;
                faceIndices.clear();
                vertexPositions.clear();
                meshAdapter.volumeToMesh(data, 2, 2, 2, 0, faceIndices, vertexPositions);
                assertEquals("Corner " + i, 3, faceIndices.size());
                assertEquals(9, vertexPositions.size());
            }
        }

        @Test
        public void testVolumeToMeshSmall() {
            float data[] = new float[8];
            IntegerArrayList faceIndices = new IntegerArrayList();
            DoubleArrayList vertexPositions = new DoubleArrayList();
            for (int i = 0; i < 256; ++i)
            {
                for (int j = 0; j < 8; ++j)
                {
                    data[j] = ((i >> j) % 2) * 2 - 1;
                }
                faceIndices.clear();
                vertexPositions.clear();
                Geometry.volumeToMesh(data, 2, 2, 2, 0, faceIndices, vertexPositions);
            }
        }
    }
}
