/*******************************************************************************
 * Copyright (c) 2019 Paul Stahr
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import geometry.Vector3d;
/**
 * Diese Klasse enth\u00E4lt Methoden, die die Verwendung von Buffern erleichtern
 *
 * @author Paul  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF ORStahr
 * @version 04.02.2012
 */
/**
 * @author paul
 *
 */
public abstract class Buffers
{
	public static final ByteBuffer NULL_POINTER = ByteBuffer.allocateDirect(0);
    private Buffers(){}

    /**
     * Erzeugt einen direkten Float Buffer
     * @param elements die Anzahl der Float Elemte
     */
    public static final FloatBuffer createFloatBuffer(int elements){
        return ByteBuffer.allocateDirect(elements<<2).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    /**
     * Creates a set of direct FloatBuffers
     * @param elements number of elements for each buffer
     * @param count number of elements overall
     */
    public static final FloatBuffer[] createFloatBuffer(int elements, int count){
        return createFloatBuffer(elements, new FloatBuffer[count]);
    }

    public static final FloatBuffer[] createFloatBuffer(int elements, FloatBuffer res[]){
        ByteBuffer bb =  ByteBuffer.allocateDirect((elements * res.length)<<2).order(ByteOrder.nativeOrder());
        for (int i = 0; i < res.length; ++i)
        {
            ((java.nio.Buffer)bb).position((i * elements) << 2);
            ((java.nio.Buffer)bb).limit(((i + 1) * elements) << 2);
            res[i] = bb.asFloatBuffer();
        }
        return res;
    }

    public static final void put(IntBuffer buf, Vector3d vec, int pos)
    {
    	buf.put(pos++, (int)vec.x).put(pos++, (int)vec.y).put(pos++, (int)vec.z);
    }

    public static final void put(ShortBuffer buf, Vector3d vec, int pos)
    {
    	buf.put(pos++, (short)vec.x).put(pos++, (short)vec.y).put(pos++, (short)vec.z);
    }

    public static final void putRev(ShortBuffer buf, Vector3d vec, int pos)
    {
    	buf.put(pos++, (short)vec.z).put(pos++, (short)vec.y).put(pos++, (short)vec.x);
    }

    public static final void putRev(FloatBuffer buf, Vector3d vec, int pos)
    {
    	buf.put(pos++, (float)vec.z).put(pos++, (float)vec.y).put(pos++, (float)vec.x);
    }

    public static final void putRev(IntBuffer buf, Vector3d vec, int pos)
    {
    	buf.put(pos++, (int)vec.z).put(pos++, (int)vec.y).put(pos++, (int)vec.x);
    }

    public static final void get(IntBuffer buf, Vector3d vec, int pos)
    {
    	vec.x = buf.get(pos++);
    	vec.y = buf.get(pos++);
    	vec.z = buf.get(pos++);
    }

    public static final void getRev(IntBuffer buf, Vector3d vec, int pos)
    {
    	vec.z = buf.get(pos++);
    	vec.y = buf.get(pos++);
    	vec.x = buf.get(pos++);
    }


    public static final void getRev(FloatBuffer buf, Vector3d vec, int pos)
    {
    	vec.z = buf.get(pos++);
    	vec.y = buf.get(pos++);
    	vec.x = buf.get(pos++);
    }

    public static final void get(ShortBuffer buf, Vector3d vec, int pos)
    {
    	vec.x = buf.get(pos++);
    	vec.y = buf.get(pos++);
    	vec.z = buf.get(pos++);
    }

    public static final void getRev(ShortBuffer buf, Vector3d vec, int pos)
    {
    	vec.z = buf.get(pos++);
    	vec.y = buf.get(pos++);
    	vec.x = buf.get(pos++);
    }


    /**
     * Creates a direct float buffer with data as content
     * @param data
     * @return The Direct FloatBuffer
     */
    public static final FloatBuffer createFloatBuffer(float... data){
        return fillFloatBuffer(createFloatBuffer(data.length), data);
    }

    public static final FloatBuffer ensureCapacity(int elements, FloatBuffer floatBuffer)
    {
    	if (floatBuffer == null || elements > floatBuffer.capacity())
    	{
    		return createFloatBuffer(elements);
    	}
    	floatBuffer.limit(elements);
    	return floatBuffer;
    }

	public static final IntBuffer ensureCapacity(int elements, IntBuffer intBuffer) {
    	if (intBuffer == null || elements > intBuffer.capacity())
    	{
    		return createIntBuffer(elements);
    	}
    	intBuffer.limit(elements);
    	return intBuffer;
    }

    /**
     * Fills a FloatBuffer with values
     * @param buf Buffer to be filled
     * @param data Data wich are put into the buffer
     * @return The given FloatBuffer
     */
    public static final FloatBuffer fillFloatBuffer(FloatBuffer buf, float... data){
        for (int i=0;i<data.length;i++)
            buf.put(i,data[i]);
        return buf;
    }

    public static final DoubleBuffer createDoubleBuffer(int elements){
        return ByteBuffer.allocateDirect(elements<<3).order(ByteOrder.nativeOrder()).asDoubleBuffer();
    }

    public static final DoubleBuffer createDoubleBuffer(double data[]){
        return fillDoubleBuffer(createDoubleBuffer(data.length), data);
    }

    public static final DoubleBuffer fillDoubleBuffer(DoubleBuffer buf, double data[]){
        for (int i=0;i<data.length;i++)
            buf.put(i,data[i]);
        return buf;
    }

    public static final DoubleBuffer fillDoubleBuffer(DoubleBuffer buf, double data[], int size){
        for (int i=0;i<size;i++)
            buf.put(i,data[i]);
        return buf;
    }

    public static final FloatBuffer fillFloatBuffer(FloatBuffer buf, float[] data, int size) {
        for (int i=0;i<size;i++)
            buf.put(i,data[i]);
        return buf;
    }

    /**
     * Creates a direct IntBuffer
     * @param elements number of ints
     * @return The created IntBuffer
     */
    public static final IntBuffer createIntBuffer(int elements){
        return ByteBuffer.allocateDirect(elements<<2).order(ByteOrder.nativeOrder()).asIntBuffer();
    }

    /**
     * Creates a direct ShortBuffer
     * @param elements number of shorts
     * @return The created ShortBuffer
     */
    public static final ShortBuffer createShortBuffer(int elements){
        return ByteBuffer.allocateDirect(elements<<1).order(ByteOrder.nativeOrder()).asShortBuffer();
    }

    /**
     * Creates a direct ByteBuffer
     * @param elements number of bytes
     * @return The created ByteBuffer
     */
    public static final ByteBuffer createByteBuffer(int elements){
        return ByteBuffer.allocateDirect(elements<<2).order(ByteOrder.nativeOrder());
    }

    /**
     * Creates a direct IntBuffer
     * @param data the data to put into the Buffer
     * @return The created IntBuffer
     */
    public static final IntBuffer createIntBuffer(int data[]){
        return fillIntBuffer(createIntBuffer(data.length), data);
    }


    /**
     * Creates a direct FloatBuffer
     * @param data the data to put into the Buffer
     * @return The created FloatBuffer
     */
    public static final FloatBuffer createFloatBuffer(int data[]){
        return fillFloatBuffer(createFloatBuffer(data.length), data);
    }

    /**
     * Creates a direct ByteBuffer
     * @param data the data to put into the Buffer
     * @return The created ByteBuffer
     */
    public static final ByteBuffer createByteBuffer(byte data[]){
        return fillByteBuffer(createByteBuffer(data.length), data, data.length);
    }

    public static final IntBuffer fillIntBuffer(IntBuffer buf, int data[]){
        for (int i=0;i<data.length;i++)
            buf.put(i,data[i]);
        return buf;
    }

    public static final FloatBuffer fillFloatBuffer(FloatBuffer buf, int data[]){
        for (int i=0;i<data.length;i++)
            buf.put(i,data[i]);
        return buf;
    }

    public static final IntBuffer fillIntBuffer(IntBuffer buf, int data[], int size){
        for (int i=0;i<size;i++)
            buf.put(i,data[i]);
        return buf;
    }

    public static final ByteBuffer fillByteBuffer(ByteBuffer buf, byte data[], int length){
        for (int i=0;i<length;i++)
            buf.put(i,data[i]);
        return buf;
    }

	public static final String toString(DoubleBuffer b) {
		StringBuilder strB = new StringBuilder(b.limit() * 2 + 2);
		strB.append('[');
		for (int i = 0; i < b.limit(); ++i)
		{
			if (i != 0)
			{
				strB.append(',');
			}
			strB.append(b.get(i));
		}
		strB.append(']');
		return strB.toString();
	}

	public static final String toString(IntBuffer b) {
		StringBuilder strB = new StringBuilder(b.limit() * 2 + 2);
		strB.append('[');
		for (int i = 0; i < b.limit(); ++i)
		{
			if (i != 0)
			{
				strB.append(',');
			}
			strB.append(b.get(i));
		}
		strB.append(']');
		return strB.toString();
	}

    public static final String toString(FloatBuffer b) {
        StringBuilder strB = new StringBuilder(b.limit() * 2 + 2);
        strB.append('[');
        for (int i = 0; i < b.limit(); ++i)
        {
            if (i != 0)
            {
                strB.append(',');
            }
            strB.append(b.get(i));
        }
        strB.append(']');
        return strB.toString();
    }
}
