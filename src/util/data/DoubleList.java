package util.data;

public interface DoubleList extends PrimitiveList{
	@Override
    public int size();

    @Override
    public double getD(int index);

    default double getD(long index) {return getD((int)index);}

    @Override
    default float getF(int index) {return (float)getD(index);}

    @Override
    default void setElem(int index, float value) {setElem(index, (double)value);}

	@Override
    public void setElem(int index, double value);

    default double[] toArray(double data[], int offset, long from, long to) {
        if (data.length < to - from + offset) {data = new double[(int)(to - from + offset)];}
        for (long i = from; i < to; ++i){data[offset++] = getD(i);}
        return data;
    }

    default float[] toArray(float[] data, int offset, long from, long to) {
        if (data.length < to - from + offset) {data = new float[(int)(to - from + offset)];}
        for (long i = from; i < to; ++i){data[offset++] = (float)getD(i);}
        return data;
    }

    @Override
    default boolean addTuple(float value0, float value1, float value2) {return addTuple((double)value0, (double)value1, (double)value2);}

    @Override
    default double[] toArrayD() {
        double res[] = new double[size()];
        for (int i = 0; i < res.length; ++i) {res[i] = getD(i);}
        return res;
    }
}
