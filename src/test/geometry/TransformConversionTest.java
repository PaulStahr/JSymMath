package test.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.geom.AffineTransform;

import org.junit.Test;

import geometry.Matrix3x2d;
import geometry.TransformConversion;

public class TransformConversionTest {
    @Test
    public void testCompatibility()
    {
        AffineTransform at = new AffineTransform(1,2,3,4,5,6);
        Matrix3x2d mat3d = new Matrix3x2d();
        TransformConversion.copy(at, mat3d);
        at.translate(3.5,4.5);
        assertTrue(mat3d.invert(mat3d));
        mat3d.postTranslate(-3.5, -4.5);
        assertTrue(mat3d.invert(mat3d));
             //at.rotate(2);
        //mat3d.rotateZ(2);
        assertEquals(at + "!=" + mat3d, 0,TransformConversion.distQ(at, mat3d), 0.001);
    }
}
