package util.data;

public interface DoubleList{
	public int size();

    public double getD(int index);

    default double getD(long index) {return getD((int)index);}

	public void setElem(int index, double value);

    default double[] toArray(double data[], int offset, long from, long to) {
        if (data.length < to - from + offset) {data = new double[(int)(to - from + offset)];}
        for (long i = from; i < to; ++i){data[offset++] = getD(i);}
        return data;
    }
}
