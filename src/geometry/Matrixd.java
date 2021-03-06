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
package geometry;

import util.data.DoubleList;

public interface Matrixd<T> extends DoubleList, Cloneable{
 	public double get(int row, int col);

	public int rows();

	public int cols();

	public void setRowMajor(double[][] o);

	public void set(int i, int j, double d);

	public void set(Matrixd<?> o);

	public void dot(T lhs, T rhs);

    public void dotl(T lhs);

    public void dotr(T rhs);

	public Matrixd<T> clone();

    @Override
    default boolean addTuple(double xp, double yp, double zp) {throw new UnsupportedOperationException();}

    @Override
    default void setSize(int l) {throw new UnsupportedOperationException();}
}
