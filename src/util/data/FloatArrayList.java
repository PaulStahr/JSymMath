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
package util.data;

import java.nio.FloatBuffer;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.function.Predicate;

import util.ArrayUtil;
import util.Buffers;

public class FloatArrayList extends AbstractList<Float> implements FloatList{
	private float data[];
	private int length;

	public void fill(FloatBuffer buf){Buffers.fillFloatBuffer(buf, data, length);}

	public FloatArrayList(){this(5);}

	public FloatArrayList(int initialElements){data = new float[initialElements];}

	public float pop(){
		float last = data[length - 1];
		--length;
		return last;
	}

	@Override
	public Float set(int index, Float value){
		float old = data[index];
		data[index] = value;
		return old;
	}

	public float set(int index, float value){
		float old = data[index];
		data[index] = value;
		return old;
	}

	@Override
	public boolean add(Float value){return add((float)value);}

	public void add(FloatList dl){
		int size = dl.size();
		enlargeTo(this.length + size);
		for (int i = 0; i < dl.size(); ++i)
		{
			data[length++] = dl.getF(i);
		}
	}

    private void enlargeTo(int length)
    {
        try {
            if (length > data.length){this.data = Arrays.copyOf(this.data, Math.max(length, data.length * 2));}
        }catch(OutOfMemoryError e) {
            System.err.print("Out of memory error" + e);
            this.data = Arrays.copyOf(this.data, Math.max(length, data.length * 3/2));
        }
    }

	public boolean add(float value){
		if (length == data.length){
			data = Arrays.copyOf(data, Math.max(data.length + 1, data.length * 2));
		}
		data[length++] = value;
		return true;
	}

	@Override
    public boolean addTuple(float value0, float value1, float value2){
		if (length + 3 > data.length){data = Arrays.copyOf(data, Math.max(length + 3, data.length * 2));}
		data[length++] = value0;
		data[length++] = value1;
		data[length++] = value2;
		return true;
	}

	@Override
	public Float get(int index) {
		if (index >= length)
			throw new ArrayIndexOutOfBoundsException(index);
		return data[index];
	}

	@Override
    public float getF(int index){
		if (index >= length)
			throw new ArrayIndexOutOfBoundsException(index);
		return data[index];
	}

	public final float sum()
	{
		float sum = 0;
		for (int i = 0; i < length; ++i)
		{
			sum += data[i];
		}
		return sum;
	}

	public final float diffSumQ(float avarage)
	{
		float var = 0;
		for (int i = 0; i < length; ++i)
		{
			float diff = data[i] - avarage;
			var += diff * diff;
		}
		return var;
	}

	@Override
	public int size() {return length;}

	public boolean contains(Float value){return indexOf((float)value) != -1;}

	public boolean contains(float value){return indexOf(value) != -1;}

	public int indexOf(Float value){return indexOf((float)value);}

	public int indexOf(float value){return ArrayUtil.linearSearch(data, 0, length, value);}

	@Override
    public double[] toArrayD() {double res[] = new double[size()]; for (int i = 0; i< res.length; ++i) {res[i] = data[i];} return res;}

	@Override
	public void clear(){length = 0;}

	@Override
    public void setSize(int l) {
		if (data.length < l){data = Arrays.copyOf(data, l);}
        length = l;
	}

	@Override
	public void setElem(int index, float value) {data[index] = value;}

    @Override
    public boolean removeIf(Predicate<? super Float> predicate) {
        int oldLength = length;
        length = ArrayUtil.removeIf(data, 0, length, predicate);
        return oldLength != length;
    }

	public float average() {
		return sum() / size();
	}

    @Override
    public float[] toArray(float[] data, int offset, long from, long to) {
        if (to > length) {throw new IndexOutOfBoundsException();}
        data = ArrayUtil.ensureLength(data, (int)(to - from + offset));
        System.arraycopy(this.data, (int)from, data, offset, (int)(to - from));
        return data;
    }

}
