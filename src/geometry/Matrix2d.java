package geometry;

public class Matrix2d implements Matrixd<Matrix2d>{
	double m00, m01, m10, m11;

	public Matrix2d(){this(1,0,0,1);}

	public Matrix2d(Matrix2d init)
	{
	    this.m00 = init.m00; this.m01 = init.m01;
	    this.m10 = init.m10; this.m11 = init.m11;
	}

	public Matrix2d(double x0, double x1, double y0, double y1){
		this.m00 = x0; this.m01 = x1;
		this.m10 = y0; this.m11 = y1;
	}

	@Override
    public final double get(int x, int y) {
		switch(x){
			case 0:switch(y){case 0:return m00;case 1:return m01;default: throw new ArrayIndexOutOfBoundsException(y);}
			case 1:switch(y){case 0:return m10;case 1:return m11;default: throw new ArrayIndexOutOfBoundsException(y);}
		}
		throw new ArrayIndexOutOfBoundsException(x);
	}

	@Override
    public final int cols() {return 2;}

	@Override
    public final int rows() {return 2;}


	@Override
    public final void setRowMajor(final double mat[][]){
		m00 = mat[0][0]; m01 = mat[1][0];
		m10 = mat[0][1]; m11 = mat[1][1];
	}


	@Override
    public final void set(int x, int y, double value){
		switch(x){
			case 0:switch(y){case 0:m00 = value;return;case 1:m01 = value;return;case 2:default: throw new ArrayIndexOutOfBoundsException(y);}
			case 1:switch(y){case 0:m10 = value;return;case 1:m11 = value;return;case 2:return;default: throw new ArrayIndexOutOfBoundsException(y);}
		}
		throw new ArrayIndexOutOfBoundsException(x);
	}


	public final void set(Matrix2d o) {
		this.m00 = o.m00;this.m01 = o.m01;
		this.m10 = o.m10;this.m11 = o.m11;
	}


	@Override
	public void set(Matrixd<?> o) {
		if (o instanceof Matrix2d)
		{
			set((Matrix2d) o);
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

	  public final void dotl(Matrix2d lhs)
	   {
	       double x = lhs.m00 * m00 + lhs.m01 * m10;
	       double y = lhs.m10 * m00 + lhs.m11 * m10;
	            m00 = x;       m10 = y;
	              x = lhs.m00 * m01 + lhs.m01 * m11;
	              y = lhs.m10 * m01 + lhs.m11 * m11;
	            m01 = x;       m11 = y;
	   }

	   public final void dotr(Matrix2d rhs)
	   {
	       double v0 = m00 * rhs.m00 + m01 * rhs.m10;
	       double v1 = m00 * rhs.m01 + m01 * rhs.m11;
	                   m00 = v0;      m01 = v1;
	              v0 = m10 * rhs.m00 + m11 * rhs.m10;
	              v1 = m10 * rhs.m01 + m11 * rhs.m11;
	                   m10 = v0;      m11 = v1;
	   }

	   @Override
	public final void dot(Matrix2d lhs, Matrix2d rhs) {
	       if (lhs == this){dotl(rhs);return;}
	       if (rhs == this){dotr(lhs);return;}
	       m00 = lhs.m00 * rhs.m00 + lhs.m01 * rhs.m10;
	       m01 = lhs.m00 * rhs.m01 + lhs.m01 * rhs.m11;
	       m10 = lhs.m10 * rhs.m00 + lhs.m11 * rhs.m10;
	       m11 = lhs.m10 * rhs.m01 + lhs.m11 * rhs.m11;
	   }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public final double getD(int index) {
        switch(index)
        {
        case 0: return m00; case 1: return m01;
        case 2: return m10; case 3: return m11;
        default:throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public void setElem(int index, double value) {
        switch(index)
        {
        case 0: m00 = value; return; case 1: m01 = value; return;
        case 2: m10 = value; return; case 3: m11 = value; return;
        default:throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public Matrix2d clone() {
        return new Matrix2d(this);
    }
}
