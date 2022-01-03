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

import maths.algorithm.Calculate;
import util.data.DoubleArrayList;
import util.data.DoubleList;

public final class Matrix3x2d implements Matrixd<Matrix3x2d>, DoubleList{
    public double m00, m01, m02;
    public double m10, m11, m12;

    public Matrix3x2d(){this(1,0,0,0,1,0);}

    public Matrix3x2d(double diag){this(diag,0,0,0,diag,0);}

    public Matrix3x2d(Matrix3x2d m) {
        this.m00 = m.m00;this.m01 = m.m01;this.m02 = m.m02;
        this.m10 = m.m10;this.m11 = m.m11;this.m12 = m.m12;
    }

    public Matrix3x2d(double x0, double x1, double x2, double y0, double y1, double y2){
        this.m00 = x0;this.m01 = x1;this.m02 = x2;
        this.m10 = y0;this.m11 = y1;this.m12 = y2;
    }

    public final void set(Matrix3d mat)
    {
        this.m00 = mat.m00;this.m01 = mat.m01;this.m02 = mat.m02;
        this.m10 = mat.m10;this.m11 = mat.m11;this.m12 = mat.m12;
    }

    public final void set(double x0, double x1, double x2, double y0, double y1, double y2){
        this.m00 = x0;this.m01 = x1;this.m02 = x2;
        this.m10 = y0;this.m11 = y1;this.m12 = y2;
    }

    public final boolean invert(Matrix3x2d read)
    {
        double [] mat = new double[size() * 2];
        read.getColMajor(mat, 0, 6);
        mat[3] = mat[10] = 1;
        if (Calculate.toRREF(mat, 2) != 2) {return false;}
        setColMajor(mat, 3, 6);
        this.m02 = -mat[2];
        this.m12 = -mat[8];
        return true;
    }

    @Override
    public void set(Matrixd<?> o) {
        if (o instanceof Matrix3x2d)
        {
            set((Matrix3x2d) o);
        }
        else
        {
            int cols = Math.min(o.cols(), cols()), rows = Math.min(o.rows(), rows());
            for (int i = 0; i < rows; ++i)
            {
                for (int j = 0; j < cols; ++j)
                {
                    set(i, j, o.get(i, j));
                }
            }
        }
    }

    public final void setRowMajor(double x0, double x1, double x2, double y0, double y1, double y2, double z0, double z1, double z2){
        this.m00 = x0;this.m01 = x1;this.m02 = x2;
        this.m10 = y0;this.m11 = y1;this.m12 = y2;
    }

    public final void getCol(int row, Vector2d vec)
    {
        switch (row)
        {
            case 0:vec.set(m00, m10);return;
            case 1:vec.set(m01, m11);return;
            case 2:vec.set(m02, m12);return;
            default: throw new ArrayIndexOutOfBoundsException(row);
        }
    }

    public final void getCol(int row, Vector3d vec)
    {
        switch (row)
        {
            case 0:vec.set(m00, m10, 0);return;
            case 1:vec.set(m01, m11, 0);return;
            case 2:vec.set(m02, m12, 0);return;
            default: throw new ArrayIndexOutOfBoundsException(row);
        }
    }

    public final void getRow(int col, Vector2d vec)
    {
        switch (col)
        {
            case 0: vec.set(m00, m01);return;
            case 1: vec.set(m10, m11);return;
            case 2: vec.set(0, 0);return;
            default: throw new ArrayIndexOutOfBoundsException(col);
        }
    }

    public final void getRow(int col, Vector3d vec)
    {
        switch (col)
        {
            case 0: vec.set(m00, m01, m02);return;
            case 1: vec.set(m10, m11, m12);return;
            case 2: vec.set(0, 0, 1);return;
            default: throw new ArrayIndexOutOfBoundsException(col);
        }
    }

    public final double getRowDot3(int row)
    {
        switch (row)
        {
            case 0: return m00 * m00 + m01 * m01 + m02 * m02;
            case 1: return m10 * m10 + m11 * m11 + m12 * m12;
            case 2: return 1;
            default: throw new ArrayIndexOutOfBoundsException(row);
        }
    }

    public final void getRowDot3(Vector2d vec)
    {
        vec.x = m00 * m00 + m01 * m01 + m02 * m02;
        vec.y = m10 * m10 + m11 * m11 + m12 * m12;
    }

    public final void getColDot3(Vector2d vec)
    {
        vec.x = m00 * m00 + m10 * m10;
        vec.y = m01 * m01 + m11 * m11;
    }

    public final double getColDot3(int col)
    {
        switch (col)
        {
            case 0: return m00 * m00 + m10 * m10;
            case 1: return m01 * m01 + m11 * m11;
            case 2: return m02 * m02 + m12 * m12;
            default: throw new ArrayIndexOutOfBoundsException(col);
        }
    }

    public final void setCols(Vector2d c0, Vector2d c1, Vector2d c2)
    {
        m00 = c0.x; m10 = c0.y;
        m01 = c1.x; m11 = c1.y;
        m02 = c2.x; m12 = c2.y;
    }

    public final void setCol(int col, Vector2d vec)
    {
        switch (col)
        {
            case 0:m00 = vec.x; m10 = vec.y; return;
            case 1:m01 = vec.x; m11 = vec.y; return;
            case 2:m02 = vec.x; m12 = vec.y; return;
            default: throw new ArrayIndexOutOfBoundsException(col);
        }
    }

    public final void setCol(int col, Vector3d vec)
    {
        switch (col)
        {
            case 0:m00 = vec.x; m10 = vec.y; return;
            case 1:m01 = vec.x; m11 = vec.y; return;
            case 2:m02 = vec.x; m12 = vec.y; return;
            default: throw new ArrayIndexOutOfBoundsException(col);
        }
    }

    public final void setRow(int col, Vector2d vec)
    {
        switch (col)
        {
            case 0: m00 = vec.x; m01 = vec.y;return;
            case 1: m10 = vec.x; m11 = vec.y;return;
            default: throw new ArrayIndexOutOfBoundsException(col);
        }
    }

    public final void setRow(int col, Vector3d vec)
    {
        switch (col)
        {
            case 0: m00 = vec.x; m01 = vec.y; m02 = vec.z; return;
            case 1: m10 = vec.x; m11 = vec.y; m12 = vec.z; return;
            default: throw new ArrayIndexOutOfBoundsException(col);
        }
    }

    public final void setRow(int row, double x, double y)
    {
        switch (row)
        {
            case 0: m00 = x; m01 = y;return;
            case 1: m10 = x; m11 = y;return;
            default: throw new ArrayIndexOutOfBoundsException(row);
        }
    }

    public final void setCol(int col, double x, double y)
    {
        switch (col)
        {
            case 0: m00 = x; m10 = y;return;
            case 1: m01 = x; m11 = y;return;
            case 2: m02 = x; m12 = y;return;
            default: throw new ArrayIndexOutOfBoundsException(col);
        }
    }

    public final void setRows(Vector2d x, Vector2d y)
    {
            m00 = x.x; m01 = x.y;
            m10 = y.x; m11 = y.y;
    }

    public final void setColMajor(final double mat[][]){
        m00 = mat[0][0]; m01 = mat[0][1]; m02 = mat[0][2];
        m10 = mat[1][0]; m11 = mat[1][1]; m12 = mat[1][2];
    }

    public final void setColMajor(final double mat[]){
        m00 = mat[0];  m01 = mat[1];  m02 = mat[2];
        m10 = mat[3];  m11 = mat[4];  m12 = mat[5];
    }

    public final void setColMajor(final double mat[][], int row, int col){
        m00 = mat[0 + row][col]; m01 = mat[0 + row][1 + col]; m02 = mat[0 + row][2 + col];
        m10 = mat[1 + row][col]; m11 = mat[1 + row][1 + col]; m12 = mat[1 + row][2 + col];
    }

    public final void setColMajor(final double mat[], int pos, int stride){
        m00 = mat[pos]; m01 = mat[pos+1]; m02 = mat[pos+2];pos += stride;
        m10 = mat[pos]; m11 = mat[pos+1]; m12 = mat[pos+2];pos += stride;
    }

    public final void getColMajor(final double mat[][]){
        mat[0][0] = m00; mat[0][1] = m01; mat[0][2] = m02;
        mat[1][0] = m10; mat[1][1] = m11; mat[1][2] = m12;
    }

    public final void getColMajor(final double mat[]){
        mat[0]  = m00; mat[1] = m01;  mat[2]  = m02;
        mat[3]  = m10; mat[4] = m11;  mat[5]  = m12;
    }

    public final void getColMajor(final double mat[], int begin, int stride){
        mat[begin] = m00; mat[begin+1] = m01; mat[begin+2] = m02;begin += stride;
        mat[begin] = m10; mat[begin+1] = m11; mat[begin+2] = m12;begin += stride;
    }

    @Override
    public final void setRowMajor(final double mat[][]){
        m00 = mat[0][0]; m01 = mat[1][0]; m02 = mat[2][0];
        m10 = mat[0][1]; m11 = mat[1][1]; m12 = mat[2][1];
    }

    public final void setRowMajor(final double mat[]){
        m00 = mat[0]; m01 = mat[2]; m02 = mat[4];
        m10 = mat[1]; m11 = mat[3]; m12 = mat[5];
    }

    public final void setRowMajor(final double mat[][], int row, int col){
        m00 = mat[row][0 + col]; m01 = mat[1 + row][0 + col]; m02 = mat[2 + row][0 + col];
        m10 = mat[row][1 + col]; m11 = mat[1 + row][1 + col]; m12 = mat[2 + row][1 + col];
    }

    public final void getRowMajor(final double mat[][]){
        mat[0][0] = m00; mat[1][0] = m01; mat[2][0] = m02;
        mat[0][1] = m10; mat[1][1] = m11; mat[2][1] = m12;
    }

    public final void rdotAffine(Vector2f v)    {rdotAffine(v.x, v.y, v);}

    public final void rdotAffine(double x, double y, Vector2f v){
        v.x = (float)(m00 * x + m10 * y + m02);
        v.y = (float)(m01 * x + m11 * y + m12);
    }

    public final double rdotX(double x, double y, double z){return m00 * x + m01 * y + m02 * z;}
    public final double rdotY(double x, double y, double z){return m10 * x + m11 * y + m12 * z;}
    public final double rdotZ(double x, double y, double z){return z;}
    public final double rdotAffineX(double x, double y){return m00 * x + m01 * y + m02;}
    public final double rdotAffineY(double x, double y){return m10 * x + m11 * y + m12;}
    public final double rdotAffineZ(double x, double y){return 1;}
    public final double ldotX(double x, double y){return m00 * x + m10 * y;}
    public final double ldotY(double x, double y){return m01 * x + m11 * y;}
    public final double ldotZ(double x, double y){return m02 * x + m12 * y;}
    public final double rdotX(double x, double y){return m00 * x + m01 * y;}
    public final double rdotY(double x, double y){return m10 * x + m11 * y;}
    public final double rdotZ(double x, double y){return 0;}

    public final void preTranslate(double x, double y)
    {
        m02 += x * m00 + y * m01;
        m12 += x * m10 + y * m11;
    }

    public final void postTranslate(double x, double y){
        m02 += x; m12 += y;
    }

    public final void preScale(double x, double y) {
        m00 *= x; m01 *= y;
        m10 *= x; m11 *= y;
    }

    public final void postScale(double x, double y)
    {
        m00 *= x; m01 *= x; m02 *= x;
        m10 *= y; m11 *= y; m12 *= y;
    }

    public final void ldot(Vector3d vector){
        final double x = vector.x, y = vector.y, z = vector.z;
        vector.x = m00 * x + m10 * y + z;
        vector.y = m01 * x + m11 * y + z;
        vector.z = m02 * x + m12 * y + z;
    }

    public final void rdot(Vector3d vector){
        final double x = vector.x, y = vector.y, z = vector.z;
        vector.x = m00 * x + m01 * y + m02 * z;
        vector.y = m10 * x + m11 * y + m12 * z;
    }

    public final void ldotAffine(Vector2d vector)                       {ldotAffine(vector.x, vector.y, vector);}
    public final void rdotAffine(Vector2d vector)                       {rdotAffine(vector.x, vector.y, vector);}
    public void rdotAffine(float[] position, int i, Vector2d result)    {rdotAffine(position[i], position[i + 1], result);}
    public void rdotAffine(double[] position, int i, Vector2d result)   {rdotAffine(position[i], position[i + 1], result);}
    public void rdotAffine(DoubleList position, int i, Vector2d result) {rdotAffine(position.getD(i), position.getD(i + 1), result);}
    public final void ldot(Vector2d vector)                             {ldot(vector.x, vector.y, vector);}

    public final void rdotAffine(double x, double y, Vector2d vector){
        vector.x = m00 * x + m01 * y + m02;
        vector.y = m10 * x + m11 * y + m12;
    }

    public final void ldot(double x, double y, Vector2d vector)
    {
        vector.x = m00 * x + m10 * y;
        vector.y = m01 * x + m11 * y;
    }

    public final void rdot(double x, double y, Vector2d result)
    {
        result.x = m00 * x + m01 * y;
        result.y = m10 * x + m11 * y;
    }

    public void rdot(double x, double y, float[] out, int i) {
        out[i++] = (float)(m00 * x + m01 * y);
        out[i++] = (float)(m10 * x + m11 * y);
    }

    public void rdot(Vector2d in, float result[], int i)    {rdot(in.x, in.y, result, i);}
    public void rdot(Vector2d in, double result[], int i)   {rdot(in.x, in.y, result, i);}
    public void rdot(Vector2d in, DoubleList result, int i) {rdot(in.x, in.y, result, i);}
    public void rdot(Vector2d in, Object result, int i)     {rdot(in.x, in.y, result, i);}

    public void rdot(double x, double y, double[] out, int i) {
        out[i++] = m00 * x + m01 * y;
        out[i++] = m10 * x + m11 * y;
    }

    public void rdot(double x, double y, DoubleList out, int i) {
        out.setElem(i++, m00 * x + m01 * y);
        out.setElem(i++, m10 * x + m11 * y);
    }

    public final void rdot(Vector2d vector){rdot(vector.x, vector.y, vector);}

    public final void rdot(double x, double y, Object out, int pos)
    {
        if (out instanceof float[])        {rdot(x, y, out, pos);}
        else if (out instanceof double[])  {rdot(x, y, (double[])out, pos);}
        else if (out instanceof DoubleList){rdot(x, y, (DoubleList)out, pos);}
        else                               {throw new IllegalArgumentException(out.getClass().toString());}
    }

    public final void rdot(Object in, int pos, Vector2d out)
    {
        if (in instanceof float[])         {rdot((float[])in, pos, out);}
        else if (in instanceof double[])   {rdot((double[])in, pos, out);}
        else if (in instanceof DoubleList) {rdot((DoubleList)in, pos, out);}
        else                               {throw new IllegalArgumentException(in.getClass().toString());}
    }

    public final void rdot(float in[], int index, Vector2d vector)     {rdot(in[index], in[index + 1], vector);}
    public final void rdot(double in[], int index, Vector2d vector)    {rdot(in[index], in[index + 1], vector);}
    public final void rdot(DoubleList in, int index, Vector2d vector)  {rdot(in.getD(index), in.getD(index + 1), vector);}

    public final void rdotAffine(Object in, int pos, Vector2d out)
    {
        if      (in instanceof float[])     {rdotAffine((float[])in, pos, out);}
        else if (in instanceof double[])    {rdotAffine((double[])in, pos, out);}
        else if (in instanceof DoubleList)  {rdotAffine((DoubleList)in, pos, out);}
        else{throw new IllegalArgumentException(in.getClass().toString());}
    }

    public final void rdotAffine(Vector2d in, Object out, int pos)
    {
        if      (out instanceof float[])            {rdotAffine(in, (float[])out, pos);}
        else if (out instanceof double[])   {rdotAffine(in, (double[])out, pos);}
        else if (out instanceof DoubleList) {rdotAffine(in, (DoubleList)out, pos);}
        else    {throw new IllegalArgumentException(in.getClass().toString());}
    }



    public final void ldotAffine(Vector2d vector, Vector2d out)             {ldotAffine(vector.x, vector.y, out);}
    public final void rdotAffine(Vector2d vector, Vector2d out)             {rdotAffine(vector.x, vector.y, out);}
    public final void rdotAffine(Vector2d vector, float out[], int index)   {rdotAffine(vector.x, vector.y, out, index);}
    public final void rdotAffine(Vector2d vector, double out[], int index)  {rdotAffine(vector.x, vector.y, out, index);}
    public final void rdotAffine(Vector2d vector, DoubleList out, int index){rdotAffine(vector.x, vector.y, out, index);}
    public final void ldotAffine(Vector2d vector, float out[], int index)   {ldotAffine(vector.x, vector.y, out, index);}

    public final void ldotAffine(double x, double y, Vector2d out){
        out.x = m00 * x + m10 * y;
        out.y = m01 * x + m11 * y;
    }

    public final void ldotAffine(double x, double y, float out[], int index){
        out[index]   = (float)(m00 * x + m10 * y);
        out[++index] = (float)(m01 * x + m11 * y);
        out[++index] = (float)(m02 * x + m12 * y);
    }

    public final void rdotAffine(double x, double y, float out[], int index){
        out[index]   = (float)(m00 * x + m01 * y + m02);
        out[++index] = (float)(m10 * x + m11 * y + m12);
    }

    public final void rdotAffine(double x, double y, double out[], int index){
        out[index]   = m00 * x + m01 * y + m02;
        out[++index] = m10 * x + m11 * y + m12;
    }

    public final void rdotAffine(double x, double y, DoubleList out, int index){
        out.setElem(index++,m00 * x + m01 * y + m02);
        out.setElem(index++,m10 * x + m11 * y + m12);
    }

    public final void ldotAffine(DoubleArrayList in, int inIndex, float[] out, int outIndex){ldotAffine(in.getD(inIndex), in.getD(++inIndex), out, outIndex);}
    public final void rdotAffine(DoubleArrayList in, int inIndex, float[] out, int outIndex){rdotAffine(in.getD(inIndex), in.getD(++inIndex), out, outIndex);}
    public final void rdotAffine(float in[], int inIndex, float[] out, int outIndex)        {rdotAffine(in[inIndex], in[inIndex + 1], out, outIndex);}
    public final void rdotAffine(double in[], int inIndex, float[] out, int outIndex)       {rdotAffine(in[inIndex], in[inIndex + 1], out, outIndex);}

    public final void ldot(double x, double y, float out[], int index){
        out[index]   = (float)(m00 * x + m10 * y);
        out[++index] = (float)(m01 * x + m11 * y);
        out[++index] = (float)(m02 * x + m12 * y);
    }

    @Override
    public final String toString(){
        StringBuilder strB = new StringBuilder(24);
        strB.append(m00).append(' ').append(m01).append(' ').append(m02).append(' ');
        strB.append(m10).append(' ').append(m11).append(' ').append(m12).append(' ');
        return strB.toString();
    }

    @Override
    public final void setElem(int i, double value)
    {
        switch(i) {
        case 0: m00 = value;return;case 1: m01 = value;return;case 2: m02 = value;return;
        case 3: m10 = value;return;case 5: m11 = value;return;case 6: m12 = value;return;
        }
        throw new ArrayIndexOutOfBoundsException(i);
    }

    @Override
    public final void set(int x, int y, double value){
        switch(x){
            case 0:switch(y){case 0:m00 = value;return;case 1:m01 = value;return;case 2:m02 = value;return;case 3:;default: throw new ArrayIndexOutOfBoundsException(y);}
            case 1:switch(y){case 0:m10 = value;return;case 1:m11 = value;return;case 2:m12 = value;return;case 3:;default: throw new ArrayIndexOutOfBoundsException(y);}
        }
        throw new ArrayIndexOutOfBoundsException(x);
    }

    @Override
    public final double get(int x, int y) {
        switch(x){
            case 0:switch(y){case 0:return m00;case 1:return m01;case 2:return m02;default: throw new ArrayIndexOutOfBoundsException(y);}
            case 1:switch(y){case 0:return m10;case 1:return m11;case 2:return m12;default: throw new ArrayIndexOutOfBoundsException(y);}
            case 2:switch(y){case 0:return 0;case 1:return 0;case 2:return 0;case 3:return 1;default: throw new ArrayIndexOutOfBoundsException(y);}
        }
        throw new ArrayIndexOutOfBoundsException(x);
    }

    public final void set(Matrix3x2d o) {
        this.m00 = o.m00;this.m01 = o.m01;this.m02 = o.m02;
        this.m10 = o.m10;this.m11 = o.m11;this.m12 = o.m12;
    }

    @Override
    public final int size() {return 6;}

    @Override
    public final int rows(){return 4;}

    @Override
    public final int cols(){return 4;}

    @Override
    public final double getD(int index) {
        switch(index)
        {
        case 0: return m00; case 1: return m01; case 2: return m02;
        case 3: return m10; case 4: return m11; case 5: return m12;
        case 6:return 0; case 7:return 0; case 8:return 1;
        default:throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public final void dotl(Matrix3x2d lhs)
    {
        double x = lhs.m00 * m00 + lhs.m01 * m10;
        double y = lhs.m10 * m00 + lhs.m11 * m10;
                            m00 = x;       m10 = y;
               x = lhs.m00 * m01 + lhs.m01 * m11;
               y = lhs.m10 * m01 + lhs.m11 * m11;
                            m01 = x;       m11 = y;
               x = lhs.m00 * m02 + lhs.m01 * m12 + lhs.m02;
               y = lhs.m10 * m02 + lhs.m11 * m12 + lhs.m12;
                            m02 = x;       m12 = y;
    }

    @Override
    public final void dotr(Matrix3x2d rhs)
    {
        double v0 = m00 * rhs.m00 + m01 * rhs.m10;
        double v1 = m00 * rhs.m01 + m01 * rhs.m11;
        double v2 = m00 * rhs.m02 + m01 * rhs.m12 + m02;
                    m00 = v0;      m01 = v1;      m02 = v2;
               v0 = m10 * rhs.m00 + m11 * rhs.m10;
               v1 = m10 * rhs.m01 + m11 * rhs.m11;
               v2 = m10 * rhs.m02 + m11 * rhs.m12 + m12;
                    m10 = v0;      m11 = v1;      m12 = v2;
    }

    @Override
    public final void dot(Matrix3x2d lhs, Matrix3x2d rhs) {
        if (lhs == this){dotl(rhs);return;}
        if (rhs == this){dotr(lhs);return;}
        m00 = lhs.m00 * rhs.m00 + lhs.m01 * rhs.m10;
        m01 = lhs.m00 * rhs.m01 + lhs.m01 * rhs.m11;
        m02 = lhs.m00 * rhs.m02 + lhs.m01 * rhs.m12 + lhs.m02;
        m10 = lhs.m10 * rhs.m00 + lhs.m11 * rhs.m10;
        m11 = lhs.m10 * rhs.m01 + lhs.m11 * rhs.m11;
        m12 = lhs.m10 * rhs.m02 + lhs.m11 * rhs.m12 + lhs.m12;
    }

    @Override
    public Matrix3x2d clone() {
        return new Matrix3x2d(this);
    }
}
