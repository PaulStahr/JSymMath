package test.util;

import static org.junit.Assert.assertEquals;

import java.nio.FloatBuffer;

import org.junit.Test;

import util.Buffers;

public class BuffersTest {
    @Test
    public void testCompile()
    {
        FloatBuffer bf[] = Buffers.createFloatBuffer(3, 4);
        assertEquals(bf.length, 4);
        for (int i = 0; i < bf.length; ++i)
        {
            assertEquals(bf[i].limit(), 3);
        }
    }
}
