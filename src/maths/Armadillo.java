package maths;

import java.io.File;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import util.Buffers;
import util.data.DoubleArrayList;
import util.data.IntegerArrayList;
import util.io.JniInterface;

public class Armadillo {
	static
	{
		try
		{
		    File path = new File("src/resources/armadillo_java.so");
			if (path.exists())
			{
			    System.out.println("Load directly");
				System.load(path.getAbsolutePath());
			}
			else
			{
                System.out.println("Load indirectly");
				JniInterface.loadLib("armadillo_java.so");
			}
		}catch(UnsatisfiedLinkError | NullPointerException | IOException e)
		{
			System.err.println("Can't load armadillo " +  e + ' ' + e.getStackTrace()[0]);
		}
	}


	public static native void spsolve(DoubleBuffer A, IntBuffer IA, IntBuffer JA, DoubleBuffer b, DoubleBuffer result);

	public static double[] solveDiffusionEquation(int width, int height, int depth, double equalityOperationResult[], boolean isGiven[])
	{
		int size = width * height * depth;
		int notGivenIndices[] = new int[size];
		int notGivenCount = 0;
		for (int i = 0; i < size; ++i)
		{
			notGivenIndices[i] = isGiven[i] ? -1 : notGivenCount++;
		}
		return solveDiffusionEquation(width, height, depth, equalityOperationResult, notGivenIndices, notGivenCount);
	}

	public static double[] solveDiffusionEquation(int width, int height, int depth, double equalityOperationResult[], int notGivenIndices[], int notGivenCount)
	{
		DoubleBuffer b = Buffers.createDoubleBuffer(notGivenCount);
		DoubleArrayList matValues = new DoubleArrayList();
		IntegerArrayList colIndices = new IntegerArrayList();
		IntBuffer rowElements = Buffers.createIntBuffer(notGivenCount + 1);
		rowElements.put(0,0);
		notGivenCount = 0;
		for (int z = 0, index = 0; z < depth; ++z)
		{
			for (int y = 0; y < height; ++y)
			{
				for (int x = 0; x < width; ++x, ++index)
				{
					if (notGivenIndices[index] != -1)
					{
						double rhs = 0;
						int neigbours = 0;
						int tmpIndex = -1;
						for (int i = 0; i < 7; ++i)
						{
							final int otherIndex;
							switch(i)
							{
								case 0: if (x == 0) 		continue; otherIndex = index - 1;				break;
								case 1: if (y == 0)			continue; otherIndex = index - width;			break;
								case 2: if (z == 0)			continue; otherIndex = index - width * height;	break;
								case 3:	tmpIndex = matValues.size(); matValues.add(0); colIndices.add(notGivenIndices[index]);continue;
								case 4: if (x == width  - 1)continue; otherIndex = index + 1;				break;
								case 5: if (y == height - 1)continue; otherIndex = index + width;			break;
								case 6: if (z == depth  - 1)continue; otherIndex = index + width * height;	break;
								default: throw new RuntimeException();
							}
							if (notGivenIndices[otherIndex] == -1)
							{
								rhs += equalityOperationResult[otherIndex];
							}
							else
							{
								matValues.add(-1);
								colIndices.add(notGivenIndices[otherIndex]);
							}
							++neigbours;
						}
						b.put(notGivenCount, rhs + equalityOperationResult[index]);
						++notGivenCount;
						matValues.set(tmpIndex, neigbours);
						rowElements.put(notGivenCount, matValues.size());
					}
				}
			}
		}
		DoubleBuffer db = Buffers.createDoubleBuffer(matValues.size());
		matValues.fill(db);
		IntBuffer ib = Buffers.createIntBuffer(colIndices.size());
		colIndices.fill(ib);
		DoubleBuffer result = Buffers.createDoubleBuffer(notGivenCount);
		//logger.debug(new StringBuilder().append(matValues.size()).append(' ').append(colIndices.size()).append(' ').append(rowElements.capacity()).toString());
		//logger.debug(new StringBuilder().append(db.capacity()).append(' ').append(ib.capacity()).append(' ').append(rowElements.capacity()).toString());
		//logger.debug("num rows:" + b.capacity() + " num cols:" + result.capacity());
		Armadillo.spsolve(db, rowElements, ib, b, result);
		for (int i = 0; i < notGivenIndices.length; ++i)
		{
			final int index = notGivenIndices[i];
			if (index != -1)
			{
				equalityOperationResult[i] = result.get(index);
			}
		}
		return equalityOperationResult;
	}
}
