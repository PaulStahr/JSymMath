package geometry;

import java.util.AbstractList;

import util.data.UniqueObjects;

public class Vector3fArrayList extends AbstractList<Vector3f>{
    private float data[] = UniqueObjects.EMPTY_FLOAT_ARRAY;

    public Vector3fArrayList(int elements)
    {
        data = new float[elements * 3];
    }

    public Vector3fArrayList(float data[])
    {
        this.data = data;
    }

    @Override
    public int size()
    {
        return data.length;
    }

    @Override
    public Vector3f get(int idx)
    {
        idx *= 3;
        return new Vector3f(data[idx], data[idx + 1], data[idx + 2]);
    }

    public Vector3f get(int idx, Vector3f out)
    {
        out.set(data, idx * 3);
        return out;
    }

    @Override
    public Vector3f set(int idx, Vector3f in)
    {
        in.write(data, idx * 3);
        return in;
    }

    @Override
    public void add(int idx, Vector3f in)
    {
        idx *= 3;
        data[idx] += in.x;
        data[idx + 1] += in.y;
        data[idx + 2] += in.z;
    }
}
