package util.data;

public interface PrimitiveList  {
    public double getD(int index);

    public float getF(int index);

    public void setElem(int index, double value);

    public void setElem(int index, float value);

    public int size();

    public boolean addTuple(double xp, double yp, double zp);

    public boolean addTuple(float value0, float value1, float value2);

    public void setSize(int i);

    public double[] toArrayD();
}
