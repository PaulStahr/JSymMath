package test.algorithm;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import geometry.Geometry;
import maths.algorithm.Calculate;

@RunWith(Parameterized.class)
public class CalculateTest {
    private final Solver s;
    public CalculateTest(Solver s)
    {
        this.s = s;
    }

    private static interface Solver{
        public int solve(double mat[], int rows) ;
    }


    @Parameters
    public static List<Solver> params() {return Arrays.asList(new Solver() {
        @Override
        public int solve(double[] mat, int rows) {
            return Calculate.toRREF(mat, rows);
        }
    },new Solver() {
        @Override
        public int solve(double[] mat, int rows) {
            double m[][] = new double[rows][mat.length / rows];
            for (int i = 0; i < mat.length / rows; ++i)
            {
                for (int j = 0; j < rows; ++j)
                {
                    m[i][j] = mat[i * rows + j];
                }
            }
            int res = Calculate.toRREF(m);
            for (int i = 0; i < mat.length / rows; ++i)
            {
                for (int j = 0; j < rows; ++j)
                {
                    mat[i * rows + j] = m[i][j];
                }
            }
            return res;
        }
    },new Solver() {
        @Override
        public int solve(double[] mat, int rows) {
            float m[][] = new float[rows][mat.length / rows];
            for (int i = 0; i < mat.length / rows; ++i)
            {
                for (int j = 0; j < rows; ++j)
                {
                    m[i][j] = (float)mat[i * rows + j];
                }
            }
            int res = Calculate.toRREF(m);
            for (int i = 0; i < mat.length / rows; ++i)
            {
                for (int j = 0; j < rows; ++j)
                {
                    mat[i * rows + j] = m[i][j];
                }
            }
            return res;
        }
    });}

    @Test
    public void testRRef()
    {
        double mat[] = {0,1,1,0,0,1,1,1,1};
        double result[] = {1,0,0,0,1,0,0,0,1};
        s.solve(mat, 3);
        assertTrue(Arrays.toString(mat) + " should be " + Arrays.toString(result), Geometry.distanceQ(mat, 0, result, 0, mat.length) < 0.01);
    }
}
