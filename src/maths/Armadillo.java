package maths;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import io.StreamUtil;
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
                String absolutePath = path.getAbsolutePath();
			    System.out.println("Load directly from " + absolutePath);
				System.load(absolutePath);
			}
			else
			{
                System.out.println("Load indirectly");
				JniInterface.loadLib("armadillo_java.so");
			}
	        System.out.println("Successfully loaded native lib");
		}catch(UnsatisfiedLinkError | NullPointerException | IOException e)
		{
			System.err.println("Can't load armadillo " +  e + ' ' + e.getStackTrace()[0]);
		}
	}


	public static enum Backend{
	    ARMADILLO, CUPY
	}



	public static native void spsolve(DoubleBuffer A, IntBuffer IA, IntBuffer JA, DoubleBuffer b, DoubleBuffer result);

	public static double[] solveDiffusionEquation(int width, int height, int depth, double equalityOperationResult[], boolean isGiven[], Backend backend)
	{
		int size = width * height * depth;
		int notGivenIndices[] = new int[size];
		int notGivenCount = 0;
		for (int i = 0; i < size; ++i)
		{
			notGivenIndices[i] = isGiven[i] ? -1 : notGivenCount++;
		}
		return solveDiffusionEquation(width, height, depth, equalityOperationResult, notGivenIndices, notGivenCount, backend);
	}

	public static double[] solveDiffusionEquation(int width, int height, int depth, double equalityOperationResult[], int notGivenIndices[], int notGivenCount, Backend backend)
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

		//logger.debug(new StringBuilder().append(matValues.size()).append(' ').append(colIndices.size()).append(' ').append(rowElements.capacity()).toString());
		//logger.debug(new StringBuilder().append(db.capacity()).append(' ').append(ib.capacity()).append(' ').append(rowElements.capacity()).toString());
		//logger.debug("num rows:" + b.capacity() + " num cols:" + result.capacity());
		try{
		    System.out.println("spsolve");
		    if (backend == Backend.ARMADILLO)
		    {
		        DoubleBuffer db = Buffers.createDoubleBuffer(matValues.size());
		        matValues.fill(db);
		        IntBuffer ib = Buffers.createIntBuffer(colIndices.size());
		        colIndices.fill(ib);
		        DoubleBuffer result = Buffers.createDoubleBuffer(notGivenCount);
		        Armadillo.spsolve(db, rowElements, ib, b, result);
		        for (int i = 0; i < notGivenIndices.length; ++i)
		        {
		            final int index = notGivenIndices[i];
		            if (index != -1)
		            {
		                equalityOperationResult[i] = result.get(index);
		            }
		        }
		    }
		    else if (backend == Backend.CUPY)
		    {
		        String path = new String("src/python/spsolve.py");
		        InputStream stream;
	            if (new File(path).exists())
	            {
	                stream = new FileInputStream(path);
	            }
	            else
	            {
	                stream = JniInterface.getResourceAsStream(path);
	            }
	            String python_script = StreamUtil.readStreamToString(stream);
	            ProcessBuilder processBuilder = new ProcessBuilder("python3", "-c", python_script);
	            processBuilder.redirectErrorStream(true);

	            Process process = processBuilder.start();

	            OutputStream oStream = process.getOutputStream();
	            OutputStreamWriter out = new OutputStreamWriter(oStream);
	            BufferedWriter outBuf = new BufferedWriter(out);

                outBuf.write(String.valueOf(matValues.size()));
                outBuf.newLine();
	            for (int i = 0;i < matValues.size(); ++i)
	            {
	                outBuf.write(String.valueOf(matValues.getD(i)));
	                outBuf.newLine();
	            }
                outBuf.write(String.valueOf(colIndices.size()));
                outBuf.newLine();
                for (int i = 0;i < matValues.size(); ++i)
                {
                    outBuf.write(String.valueOf(colIndices.getI(i)));
                    outBuf.newLine();
                }
                outBuf.write(String.valueOf(rowElements.limit()));
                outBuf.newLine();
                for (int i = 0;i < rowElements.limit(); ++i)
                {
                    outBuf.write(String.valueOf(rowElements.get(i)));
                    outBuf.newLine();
                }
                outBuf.write(String.valueOf(b.limit()));
                outBuf.newLine();
                for (int i = 0;i < b.limit(); ++i)
                {
                    outBuf.write(String.valueOf(b.get(i)));
                    outBuf.newLine();
                }
                outBuf.close();
                out.close();
                oStream.close();

	            InputStream iStream = process.getInputStream();
                InputStreamReader iRead = new InputStreamReader(iStream);
                BufferedReader reader = new BufferedReader(iRead);
	            for (int i = 0; i < notGivenIndices.length; ++i)
	            {
	                final int index = notGivenIndices[i];
                    if (index != -1)
                    {
                        String line = reader.readLine();
    	                equalityOperationResult[i] = Double.parseDouble(line);
                    }
	            }
	            reader.close();
	            iRead.close();
	            iStream.close();

	            int exitCode = process.waitFor();
	            assertEquals("No errors should be detected", 0, exitCode);
		    }
		    else {
		        throw new RuntimeException("Backend not known");
		    }
            System.out.println("successful");
		}catch(Exception e) {
		    e.printStackTrace();
		    throw new RuntimeException(e);
		}

		return equalityOperationResult;
	}
}
