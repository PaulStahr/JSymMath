package util.data;

public interface FloatList extends PrimitiveList{
	@Override
    public int size();

    @Override
    public float getF(int index);

    default float getF(long index) {return getF((int)index);}

    default double getD(long index) {return getF(index);}

    @Override
    default double getD(int index) {return getF(index);}

	@Override
    public void setElem(int index, float value);

	@Override
    default void setElem(int index, double value) {setElem(index, (float)value);}

	@Override
    public boolean addTuple(float value0, float value1, float value2);

	@Override
	default boolean addTuple(double value0, double value1, double value2){return addTuple((float)value0, (float) value1, (float) value2);}


    default double[] toArray(double data[], int offset, long from, long to) {
        if (data.length < to - from + offset) {data = new double[(int)(to - from + offset)];}
        for (long i = from; i < to; ++i){data[offset++] = getD(i);}
        return data;
    }

    default float[] toArray(float[] data, int offset, long from, long to) {
        if (data.length < to - from + offset) {data = new float[(int)(to - from + offset)];}
        for (long i = from; i < to; ++i){data[offset++] = getF(i);}
        return data;
    }

    @Override
    default double[] toArrayD() {
        double res[] = new double[size()];
        for (int i = 0; i < res.length; ++i) {res[i] = getF(i);}
        return res;
    }
}
