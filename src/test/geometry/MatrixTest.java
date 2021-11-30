package test.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import geometry.Matrix3d;
import geometry.Matrix4d;
import geometry.Vector3d;
import geometry.Vector3f;
import geometry.Vector4d;
import util.ArrayUtil;

public class MatrixTest {
    @Test
    public void testInverse3()
    {
        Matrix3d orig = new Matrix3d(2,2,-2,-1,1,1,4,-4,4);
        Matrix3d mat = new Matrix3d(orig);
        Matrix3d identity = new Matrix3d();
        Matrix3d dotprod = new Matrix3d();
        assertTrue(mat.invert(mat));
        dotprod.dot(orig,mat);
        assertTrue(orig + "!=" + mat, ArrayUtil.qdist(dotprod, 0, identity, 0, mat.size()) < 0.1);
    }

    @Test
    public void testInverse4()
    {
        Matrix4d orig = new Matrix4d(1,1,-1,1,-1,1,1,1,1,-1,1,1,1,1,1,-1);
        Matrix4d mat = new Matrix4d(orig);
        Matrix4d identity = new Matrix4d();
        Matrix4d dotprod = new Matrix4d();
        assertTrue(mat.invert(mat));
        dotprod.dot(orig,mat);
        assertTrue(orig + "!=" + mat, ArrayUtil.qdist(dotprod, 0, identity, 0, mat.size()) < 0.1);
    }

    @Test
    public void testTranslate3()
    {
        Matrix3d orig = new Matrix3d(1,1,-1,-1,1,1,1,-1,1);
        Matrix3d mat = new Matrix3d(orig);
        Matrix3d identity = new Matrix3d();
        Matrix3d dotprod = new Matrix3d();
        mat.invert();
        dotprod.dot(orig, mat);
        assertTrue(orig +"*" + mat + "=" + dotprod + "!=" + identity, ArrayUtil.qdist(dotprod, 0, identity, 0, identity.size()) < 0.1);
        orig.postTranslate(3, 4);
        mat.preTranslate(-3, -4);
        dotprod.dot(orig, mat);
        assertTrue(orig +"*" + mat + "=" + dotprod + "!=" + identity, ArrayUtil.qdist(dotprod, 0, identity, 0, identity.size()) < 0.1);
    }

    @Test
    public void testScale3()
    {
        Matrix3d orig = new Matrix3d(1,1,-1,-1,1,1,1,-1,1);
        Matrix3d mat = new Matrix3d(orig);
        Matrix3d identity = new Matrix3d();
        Matrix3d dotprod = new Matrix3d();
        assertTrue(mat.invert(mat));
        dotprod.dot(orig, mat);
        assertTrue(orig +"*" + mat + "=" + dotprod + "!=" + identity, ArrayUtil.qdist(dotprod, 0, identity, 0, identity.size()) < 0.1);
        orig.postScale(2, 4);
        mat.preScale(0.5, 0.25);
        dotprod.dot(orig, mat);
        assertTrue(orig +"*" + mat + "=" + dotprod + "!=" + identity, ArrayUtil.qdist(dotprod, 0, identity, 0, identity.size()) < 0.1);
    }

    @Test
    public void testTranslate4()
    {
        Matrix4d orig = new Matrix4d(1,1,-1,1,-1,1,1,1,1,-1,1,1,1,1,1,-1);
        Matrix4d mat = new Matrix4d(orig);
        Matrix4d identity = new Matrix4d();
        Matrix4d dotprod = new Matrix4d();
        assertTrue(mat.invert(mat));
        dotprod.dot(orig,mat);
        assertTrue(orig +"*" + mat + "=" + dotprod + "!=" + identity, ArrayUtil.qdist(dotprod, 0, identity, 0, mat.size()) < 0.1);
        orig.postTranslate(3, 4, 5);
        mat.preTranslate(-3, -4, -5);
        dotprod.dot(orig,mat);
        assertTrue(orig +"*" + mat + "=" + dotprod + "!=" + identity, ArrayUtil.qdist(dotprod, 0, identity, 0, mat.size()) < 0.1);
    }

    @Test
    public void testScale4()
    {
        Matrix4d orig = new Matrix4d(1,1,-1,1,-1,1,1,1,1,-1,1,1,1,1,1,-1);
        Matrix4d mat = new Matrix4d(orig);
        Matrix4d identity = new Matrix4d();
        Matrix4d dotprod = new Matrix4d();
        assertTrue(mat.invert(mat));
        dotprod.dot(orig,mat);
        assertTrue(orig +"*" + mat + "=" + dotprod + "!=" + identity, ArrayUtil.qdist(dotprod, 0, identity, 0, mat.size()) < 0.1);
        orig.postScale(2, 4, 8);
        mat.preScale(0.5, 0.25, 0.125);
        dotprod.dot(orig,mat);
        assertTrue(orig +"*" + mat + "=" + dotprod + "!=" + identity, ArrayUtil.qdist(dotprod, 0, identity, 0, mat.size()) < 0.1);
    }

    @Test
    public void testRdot()
    {
        Vector3d v = new Vector3d(1,2,3);
        Vector3d w = new Vector3d(v);
        Matrix4d mat = new Matrix4d();
        mat.postScale(4, 5, 6);
        w.multiply(new Vector3d(4,5,6));
        mat.postTranslate(4, 2, 5);
        w.add(4,2,5);
        Vector4d out4 = new Vector4d(v.x, v.y, v.z, 1);
        Vector3d out3 = new Vector3d(v);
        mat.rdotAffine(out3);
        assertEquals(0, ArrayUtil.qdist(w,0,out3,0,3), 1e-10);
        mat.rdot(out4);
        assertEquals(0, ArrayUtil.qdist(w,0,out4,0,3), 1e-10);
        mat.rdotAffine(v.x,v.y,v.z,out3);
        assertEquals(0, ArrayUtil.qdist(w,0,out3,0,3), 1e-10);
        Vector3f out3f = new Vector3f();
        mat.rdotAffine(v.x,v.y,v.z,out3f);
        assertEquals(0, ArrayUtil.qdist(w,0,out3f,0,3), 1e-10);
    }
}
