package util.data;

public interface IntegerList{
	public int getI(int index);

	public int size();

	public void setElem(int index, int value);

    default int[] toArray(int[] data, int offset, long from, long to) {
        if (data.length < to - from + offset) {data = new int[(int)(to - from + offset)];}
        for (long i = from; i < to; ++i){data[offset++] = getI((int)i);}
        return data;
    }
}
