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

import java.nio.IntBuffer;

import util.data.DoubleList;

/**
* @author  Paul Stahr
* @version 04.02.2012
*/

public final class Vector3d implements Vectord
{
    public double x, y, z;

    public Vector3d(){}
    public Vector3d(final double x,final double y,final double z){this.x=x;this.y=y;this.z=z;}
    public Vector3d(final Vector3f vector){set(vector);}
    public Vector3d(final Vector3d vector){set(vector);}

    @Override
	public double getD(int index)
    {
    	switch (index)
    	{
	    	case 0: return x;
	    	case 1: return y;
	    	case 2: return z;
	    	default: throw new ArrayIndexOutOfBoundsException(index);
    	}
    }

    @Override
    public void setElem(int index, double value)
    {
    	switch (index)
    	{
	    	case 0: this.x = value; return;
	    	case 1: this.y = value; return;
	    	case 2: this.z = value; return;
	    	default: throw new ArrayIndexOutOfBoundsException(index);
    	}
    }

    @Override
    public final boolean equals(Object o)
    {
    	if (o instanceof Vector3d)
    	{
    		Vector3d v = (Vector3d)o;
    		return v.x == x && v.y == y && v.z == z;
    	}
    	return false;
    }

    public void sub(final Vector3f v){x -= v.x;y -= v.y;z -= v.z;}
    public void sub(final Vector3d v){x -= v.x;y -= v.y;z -= v.z;}

    public void sqrt() {x = Math.sqrt(x);y = Math.sqrt(y);z = Math.sqrt(z);}
	public void divide(Vector3d v) 	 {x /= v.x;y /= v.y;z /= v.z;}

    public final boolean containsNaN(){return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z);}

     /**
     * erzeugt eine Normale
     */
    public static final Vector3f getNormal(final Vector3f v0, final Vector3f v1, final Vector3f v2){
        Vector3f vektor = new Vector3f();
        vektor.calcNormal (v0, v1, v2);
        return vektor;
    }

    /**
     * erzeugt eine Normale
     */
    public final void calcNormal(final Vector3f v0, final Vector3f v1, final Vector3f v2){
        final double ax = v0.x-v1.x, ay = v0.y-v1.y, az = v0.z-v1.z;
        final double bx = v1.x-v2.x, by = v1.y-v2.y, bz = v1.z-v2.z;
        x = ay*bz - az*by;
        y = az*bx - ax*bz;
        z = ax*by - ay*bx;
    }

    public final void normalize(){multiply(1/norm());}
    public final void setNorm(double length){multiply(length / norm());}
    public final double norm(){return Math.sqrt(dot());}

    public final void multiply(final double m){x*=m;y*=m;z*=m;}
	public final void multiply(Vector3d v) {x *= v.x;y *= v.y;z *= v.z;}

    public final void set(float data[], int pos){x = data[pos];y = data[pos + 1];z = data[pos + 2];}
    public final void set(double data[], int pos){x = data[pos];y = data[pos + 1];z = data[pos + 2];}

    public final void set(Object data, int pos)
    {
    	if 		(data instanceof float[])	{set((float[])data, pos);}
    	else if (data instanceof double[])	{set((double[])data, pos);}
    	else if (data instanceof DoubleList){set((DoubleList)data, pos);}
    	else{throw new IllegalArgumentException(data.getClass().toString());}
    }

    public final void write(Object data, int pos)
    {
    	if 		(data instanceof float[])	{write((float[])data, pos);}
    	else if (data instanceof double[])	{write((double[])data, pos);}
    	else if (data instanceof DoubleList){write((DoubleList)data, pos);}
    	else if (data instanceof IntBuffer)	{write((IntBuffer)data, pos);}
    	else{throw new IllegalArgumentException(data.getClass().toString());}
    }

    public final void write(final double data[], final int pos){data[pos] = x;data[pos + 1] = y;data[pos + 2] = z;}
    public final void write(float data[], int pos){data[pos] = (float)x;data[pos + 1] = (float)y;data[pos + 2] = (float)z;}
    public final void write(DoubleList list, int index)
    {
    	list.setElem(index, this.x);
    	list.setElem(index + 1, this.y);
    	list.setElem(index + 2, this.z);
    }

    public final void write(IntBuffer list, int index)
    {
    	list.put(index, 	(int)this.x);
    	list.put(index + 1, (int)this.y);
    	list.put(index + 2, (int)this.z);
    }

    /**
     * Dreht einen Vektor um eine Drehung
     */
    public final void rotateXYZEuler(Rotation3 r){
    	rotateRadiansX(r.getXRadians());
    	rotateRadiansY(r.getYRadians());
    	rotateRadiansZ(r.getZRadians());
    }

    /**
     * Dreht einen Vektor um eine Drehung
     */
    public final void rotateReverseXYZEuler(Rotation3 r){
    	rotateRadiansZ(-r.getZRadians());
    	rotateRadiansY(-r.getYRadians());
    	rotateRadiansX(-r.getXRadians());
    }

    /**
     * Rotiert den Vektor.
     * @param x die Rotation um die x-Achse
     */
    public final void rotateRadiansX(double x){
        double sin = Math.sin(x);
        double cos = Math.cos(x);
        double tmp = cos*y-sin*z;
        z = sin*y+cos*z;
        y=tmp;
    }


    /**
     * Rotiert den Vektor.
     * @param y die Rotation um die y-Achse
     */
    public final void rotateRadiansY(double y){
    	double sin = Math.sin(y);
        double cos = Math.cos(y);
        double tmp = cos*x+sin*z;
        z = cos*z-sin*x;
        x = tmp;
    }


    /**
     * Rotiert den Vektor.
     * @param z die Rotation um die z-Achse
     */
    public final void rotateRadiansZ(double z){
        double sin = Math.sin(z);
        double cos = Math.cos(z);
        double tmp = cos*x-sin*y;
        y = sin*x+cos*y;
        x = tmp;
    }

    @Override
	public final double dot() {return x * x + y * y + z * z;}
    public final double dot(Vector3d v){return x * v.x + y * v.y + z * v.z;}
    public final double dot(final double x, final double y, final double z){return this.x * x + this.y * y + this.z * z;}
	public final double dot(double[] data, int i) 	{return data[i] * x + data[i + 1] * y + data[i + 2] * z;}
	public final double dot(Vector3d v, Vector3d w) {return x * (v.x - w.x) + y * (v.y - w.y) + z * (v.z - w.z);}
	public final double dot(float[] data, int i) 	{return data[i] * x + data[i + 1] * y + data[i + 2] * z;}

    public final void set(final double x, final double y, final double z){this.x=x;this.y=y;this.z=z;}
    public final void set(DoubleList list, int index){this.x = list.getD(index);this.y = list.getD(index + 1);this.z = list.getD(index + 2);}

    public final void set(final Vector3f v){x = v.x;y = v.y;z = v.z;}
    public final void set(final Vector2d v){x = v.x;y = v.y;}
    public final void set(final Vector3d v){x = v.x;y = v.y;z = v.z;}
    public final void set(final Vector3d v, double s){x = v.x * s;y = v.y * s;z = v.z * s;}

    public final void set(final Vector3d v, final Vector3d w, double s){
        x = v.x + w.x * s;
        y = v.y + w.y * s;
        z = v.z + w.z * s;
    }

    public final void set(final double x, final double y, final double z, final Vector3d v, final double s){
        this.x = x + v.x * s;
        this.y = y + v.y * s;
        this.z = z + v.z * s;
    }

    public final void set(final Vector3d v, double s, final Vector3d w, double t){
        x = v.x * s + w.x * t;
        y = v.y * s + w.y * t;
        z = v.z * s + w.z * t;
    }

    public final void set(final Vector3d v0, final Vector3d v1, double s1, final Vector3d v2, double s2){
        x = v0.x + v1.x * s1 + v2.x * s2;
        y = v0.y + v1.y * s1 + v2.y * s2;
        z = v0.z + v1.z * s1 + v2.z * s2;
    }

    public final void set(final double x, final double y, final double z, final Vector3d v1, final double s1, final Vector3d v2, final double s2){
        this.x = x + v1.x * s1 + v2.x * s2;
        this.y = y + v1.y * s1 + v2.y * s2;
        this.z = z + v1.z * s1 + v2.z * s2;
    }

    public final void set(final Vector3d v0, final Vector3d v1, final Vector3d v2, double s2){
        x = v0.x - v1.x + v2.x * s2;
        y = v0.y - v1.y + v2.y * s2;
        z = v0.z - v1.z + v2.z * s2;
    }

    public final void set(final Vector3d v0, double s0, final Vector3d v1, double s1, final Vector3d v2, double s2){
        x = v0.x * s0 + v1.x * s1 + v2.x * s2;
        y = v0.y * s0 + v1.y * s1 + v2.y * s2;
        z = v0.z * s0 + v1.z * s1 + v2.z * s2;
    }

    public final void setDiff(final Vector3d v, final Vector3d w){x = v.x - w.x;y = v.y - w.y;z = v.z - w.z;}
    public final void setAdd (final Vector3d v, final Vector3d w){x = v.x + w.x;y = v.y + w.y;z = v.z + w.z;}
	public final void setAdd (final Vector3d v, final double x, final double y, final double z) {this.x = v.x + x;this.y = v.y + y;this.z = v.z + z;}

    public final void add(final Vector2d v){x += v.x;y += v.y;}
    public final void add(final Vector3f v){x += v.x;y += v.y;z += v.z;}
    public final Vector3d add(final Vector3d v){x += v.x;y += v.y;z += v.z;return this;}
    public final void add(final Vector3d v, double s){x += v.x * s;y += v.y * s;z += v.z * s;}

    public double[] toArrayD() {return new double[] {x,y,z};}
    public float[] toArrayF() {return new float[] {(float)x,(float)y,(float)z};}

    /**
     * Addiert zum Vektor bestimmte Werte.
     * @param vektor der Vektor der addiert werden soll
     * @param scalar der Skalar mit dem der vektor vorher multipliziert wird
     */
    public final void add(final Vector3d vektor, double scalar, Vector3d vector2, double scalar2){
        x += vektor.x * scalar + vector2.x * scalar2;
        y += vektor.y * scalar + vector2.x * scalar2;
        z += vektor.z * scalar + vector2.x * scalar2;
    }

    public final void add(final double x, final double y, final double z, final double s){this.x += x * s;this.y += y * s;this.z += z * s;}
    public final void add(final double x, final double y, final double z){this.x += x;this.y += y;this.z += z;}
    public final void add(final double x, final double y){this.x += x;this.y += y;}
	public final void add(float[] data, int index) {x += data[index];y += data[index + 1];z += data[index + 2];}
	public final void add(float[] data, int index, float s) {x += data[index] * s;y += data[index + 1] * s;z += data[index + 2] * s;}
	@Override
	public final void add(double[] data, int index) {x += data[index];y += data[index + 1];z += data[index + 2];}

    public final void cross(Vector3d v0, Vector3d v1)
    {
    	x = v0.y * v1.z - v0.z * v1.y;
    	y = v0.z * v1.x - v0.x * v1.z;
    	z = v0.x * v1.y - v0.y * v1.x;
    }

	public final void reflect(Vector3d v) {add(v, -2 * dot(v)/v.dot());}

	public final void invert(Vector3d v){x = -v.x;y = -v.y;z = -v.z;}
	public final void invert()			{x = -x;y = -y;z = -z;}

   	public final double distanceQ(Vector3d v)
	{
		double xDiff = x - v.x;
		double yDiff = y - v.y;
		double zDiff = z - v.z;
		return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
	}

	public double distance(Vector3d v) {return Math.sqrt(distanceQ(v));}
	public double distance(float[] data, int i) {return Math.sqrt(distanceQ(data, i));}

	public final double distanceQ(Vector3d v, double s)
	{
		double xDiff = x - v.x * s;
		double yDiff = y - v.y * s;
		double zDiff = z - v.z * s;
		return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
	}

	public final double distanceQ(final double scale, final double x, final double y, final double z)
	{//TODO: order of arguments is contraintuitive
		double xDiff = scale * this.x - x;
		double yDiff = scale * this.y - y;
		double zDiff = scale * this.z - z;
		return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
	}

	public final double distanceQ(final double x, final double y, final double z)
    {
        double xDiff = this.x - x;
        double yDiff = this.y - y;
        double zDiff = this.z - z;
        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
    }

	public final double distanceQ(float[] data, int index) {
		double xDiff = x - data[index];
		double yDiff = y - data[index + 1];
		double zDiff = z - data[index + 2];
		return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
	}

	public final double distanceQ(double[] data, int index) {
		double xDiff = x - data[index];
		double yDiff = y - data[index + 1];
		double zDiff = z - data[index + 2];
		return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
	}

    @Override
	public final String toString(){return toString(new StringBuilder()).toString();}
    public final StringBuilder toString(StringBuilder strB){return strB.append('(').append(x).append(',').append(y).append(',').append(z).append(')');}

	@Override
	public int size() {return 3;}

	public double acosDistance(Vector3d position, Vector3d midpoint) {
		double diffx = position.x - midpoint.x, diffy = position.y - midpoint.y, diffz = position.z = midpoint.z;
		return Math.acos(dot(diffx, diffy, diffz)/Math.sqrt((diffx * diffx + diffy * diffy + diffz * diffz)*dot()));
	}
}
