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

import java.awt.geom.Rectangle2D;
import java.text.ParseException;
import java.util.Arrays;

import maths.algorithm.Calculate;
import util.ArrayUtil;
import util.StringUtils;
import util.data.DoubleList;
import util.data.IntegerArrayList;
import util.data.PrimitiveList;

public class Geometry {
	public static final class NearestPointCalculator
	{
		final int dim;
		final double mat[];
		final double averagePos[];
		final double averageDir[];
		int nCols;
		int count;

		public NearestPointCalculator(int dim)
		{
			this.dim = dim;
			this.nCols = dim + 1;
			this.mat = new double[dim * nCols];
			this.averagePos = new double[dim];
			this.averageDir = new double[dim];
		}

		public final void reset()
		{
			Arrays.fill(mat, 0);
			Arrays.fill(averagePos, 0);
			Arrays.fill(averageDir, 0);
			count = 0;
		}

		public double[] getAveragePos(double res[])
		{
		    for (int i = 0; i < dim; ++i)
		    {
		        res[i] = averagePos[i] / count;
		    }
		    return res;
		}

		public double[] getAverageDir(double res[])
		{
		    for (int i = 0; i < dim; ++i)
		    {
		        res[i] = averageDir[i] / count;
		    }
		    return res;
		}

        /**
         Measures on a scale from 0 to one how uniform the directions are distributed
         1 means that all directions are the same and the crossing point is not well defined
         */
		public double getUniformness() {
		    double res = averageDir[0] * averageDir[0];
		    for (int i = 1; i < dim; ++i)
            {
                res += averageDir[i] * averageDir[i];
            }
            return res / (count * count);
		}

        public final void addRay(DoubleList positions, DoubleList directions, int idx)
        {
            double dirlen = 0;
            double scalar = 0;
            for (int j = 0; j < dim; ++j)
            {
                int index = idx + j;
                double dir = directions.getD(index);
                dirlen += dir * dir;
                double p = positions.getD(index);
                averagePos[j] += p;
                scalar += dir * p;
            }
            dirlen = 1 / dirlen;
            for (int j = 0; j < dim; ++j)
            {
                int index = idx + j;
                double dir = directions.getD(index) * dirlen;
                averageDir[j] += dir;
                int jc = j * nCols;
                mat[jc + dim] += scalar * dir - positions.getD(index);
                for (int k = 0; k <= j; ++k)
                {
                    mat[jc + k] += dir * directions.getD(idx + k);
                }
            }
            ++count;
        }

		public final void addRay(float positions[], float directions[], int idx)
		{
			double dirlen = 0;
			double scalar = 0;
			for (int j = 0; j < dim; ++j)
			{
				int index = idx + j;
				float dir = directions[index];
				dirlen += dir * dir;
                double p = positions[index];
                averagePos[j] += p;
				scalar += dir * p;
			}
			dirlen = 1 / dirlen;
			for (int j = 0; j < dim; ++j)
			{
				int index = idx + j;
				double dir = directions[index] * dirlen;
				averageDir[j] += dir;
				int jc = j * nCols;
				mat[jc + dim] += scalar * dir - positions[index];
				for (int k = 0; k <= j; ++k)
				{
					mat[jc + k] += dir * directions[idx + k];
				}
			}
			++count;
		}

		public int getCount(){return count;}

		@Override
        public final String toString() {
		    StringBuilder strB = new StringBuilder();
		    double mat[] = this.mat.clone();
		    prepareMatrix(mat);
		    for (int i = 0; i < dim; ++i)
		    {
		        for (int j = 0; j < nCols; ++j)
		        {
                    if (j != 0)
                    {
                        strB.append(',');
                    }
		            strB.append(mat[i * nCols + j]);
		        }
		        strB.append('\n');
		    }
		    return strB.toString();
		}

		public final int calculate(double eps)
		{
		    prepareMatrix(mat);
	        int res = Calculate.toRREF(mat, dim);
	        for (int i = 0; i < dim; ++i)
	        {
	            if (Math.abs(mat[i * nCols + i] - 1) > eps)
	            {
	                return i;
	            }
	        }
	        return res;
		}

		public final int calculate()
		{
		    return calculate(0.01);
		}

		private void prepareMatrix(double[] mat) {
		    for (int i = 0; i < dim; ++i)
            {
                mat[i * nCols + i] -= count;
                for (int j = 0; j < i; ++j)
                {
                    mat[j * nCols + i] = mat[i * nCols + j];
                }
            }
        }

        public final double get(int index){return mat[index * nCols + dim];}

		public final void get(Vectord vec)
		{
			for (int i = 0; i < dim; ++i)
			{
				vec.setElem(i, mat[i * nCols + dim]);
			}
		}

		public final void get(float[] res)
		{
			for (int i = 0; i < dim; ++i)
			{
				res[i] = (float)mat[i * nCols + dim];
			}
		}

        public final double getAveragePos(int i) {
            return averagePos[i] / count;
        }

        public final double getAverageDir(int i) {
            return averageDir[i] / count;
        }
	}

	public static final void parse(String str, Vectord vec) throws ParseException
	{
		if (str.charAt(0) != '(' || str.charAt(str.length() - 1) != ')')
		{
			throw new ParseException(str, 0);
		}
		int begin = 1; int end = str.indexOf(',');
		for (int index = 0; end != -1; ++index)
		{
			vec.setElem(index, StringUtils.parseDouble(str, begin, end));
			begin = end;
			end = str.indexOf(begin, ',');
		}
	}

	public static final void volumeToMesh(int[] data, int width, int height, int depth, double mid, IntegerArrayList faceIndices, PrimitiveList vertexPositions)
	{
		int offsets[] = new int[8];
		int vertexIndices[] = new int[width * height * 2 * 3];
		Arrays.fill(vertexIndices, -1);
		boolean inside[] = new boolean[8];
		boolean visited[] = new boolean[8];
		boolean insideConnected[] = new boolean[8];
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < (1 << i); ++j)
			{
				offsets[j + (1 << i)] = offsets[j] + (i == 0 ? 1 : i == 1 ? width : width * height);
			}
		}
		IntegerArrayList searchStack = new IntegerArrayList();
		for (int z = 0; z < depth - 1; ++z)
		{
            System.arraycopy(vertexIndices, width * height * 3, vertexIndices, 0, width * height * 3);
            Arrays.fill(vertexIndices, width * height * 3, width * height * 6, -1);
			for (int y = 0; y < height - 1; ++y)
			{
				for (int x = 0; x < width - 1; ++x)
				{
					final int index = x + (width * (y + height * z));
					boolean is_cutted = false;
					{
						boolean first = inside[0] = data[index] > mid;
						for (int bitmask = 1; bitmask < 8; ++bitmask)
						{
							is_cutted |= (inside[bitmask] = (data[index + offsets[bitmask]] > mid)) != first;
						}
					}
					if (is_cutted)
					{
						Arrays.fill(visited, false);
						for (int bitmask = 0; bitmask < 8; ++bitmask)
						{
							Arrays.fill(insideConnected, false);
							int innerVertex = bitmask;
							int inOutAxis = -1;
							if (inside[bitmask] && !visited[bitmask])
							{
								visited[bitmask] = insideConnected[bitmask] = true;
								searchStack.add(bitmask);
								while(!searchStack.isEmpty())
								{
									int current = searchStack.pop();
									for (int axis = 0; axis < 3;++axis)
									{
										int neighbour = current ^ (1 << axis);

										if (inside[neighbour] && !visited[neighbour])
										{
											searchStack.add(neighbour);
											visited[neighbour] = insideConnected[neighbour] = true;
										}
										if (!inside[neighbour])
										{
											innerVertex = current;
											inOutAxis = axis;
										}
									}
								}
							}
							if (inOutAxis == -1)
							{
								continue;
							}
							int facePoint0 = -1, facePoint1 = -1;
							final int startInnerVertex = innerVertex;
							final int startInOutAxis = inOutAxis;
							while(true)
							{
								final int outerVertex = innerVertex ^ (1 << inOutAxis);
								final int lowerIndex = innerVertex & outerVertex;
								final int higherIndex = innerVertex | outerVertex;
                                final int vIndex = (x + width * y + offsets[lowerIndex]) * 3 + inOutAxis;
                                int facePoint2 = vertexIndices[vIndex];
								if (facePoint2 == -1)
								{
									double v0 = data[index + offsets[lowerIndex]], v1 = data[index + offsets[higherIndex]];
									double alpha = (v0 - mid) / (v0 - v1);
									if (alpha < 0 || alpha > 1){throw new RuntimeException("alpha not in range: " + alpha);}
									/*if (alpha <= 0.00001) //clip near to lattice values to remove small triangles
									{
										vIndex = (cubeIndex + cubeOffsets[min]) * 3;
										facePoint2 = vertexIndices[vIndex];
									}
									if (alpha >= 0.99999) //clip near to lattice values to remove small triangles
									{
										vIndex = (cubeIndex + cubeOffsets[min]) * 3;
										facePoint2 = vertexIndices[vIndex];
									}*/
									if (facePoint2 == -1)
									{
										facePoint2 = vertexIndices[vIndex] = vertexPositions.size() / 3;
	                                    double xp = x + ((lowerIndex >> 0) & 1);
	                                    double yp = y + ((lowerIndex >> 1) & 1);
	                                    double zp = z + ((lowerIndex >> 2) & 1);
                                        int addIndex = vertexPositions.size() + inOutAxis;
										vertexPositions.addTuple(xp, yp, zp);
										vertexPositions.setElem(addIndex, vertexPositions.getD(addIndex) + alpha);
									}
								}
								if (facePoint0 == -1)
								{
                                    facePoint0 = facePoint1;
								}
								else if (facePoint2 != facePoint1 && facePoint0 != facePoint1 && facePoint0 != facePoint2)
                                {
                                    faceIndices.addTuple(facePoint0, facePoint1, facePoint2);
                                }
								facePoint1 = facePoint2;

								final int newAxis = (((innerVertex ^ (innerVertex >> 1) ^ (innerVertex >> 2)) & 1) + inOutAxis + 1)%3;
								final int newInner = innerVertex ^ (1 << newAxis);
								final int newOuter = outerVertex ^ (1 << newAxis);
	                            if (!insideConnected[newInner])
	                            {
	                                inOutAxis = newAxis;//move on outer vertex
	                            }
	                            else if (!insideConnected[newOuter])
	                            {
	                                innerVertex = newInner;//move on both vertices
	                            }
	                            else
	                            {
	                                innerVertex = newOuter;//move on inner vertex
	                                inOutAxis = newAxis;
	                            }
                                if (innerVertex == startInnerVertex && inOutAxis == startInOutAxis)
                                {
                                    break;
                                }
							}
						}
					}
				}
			}
		}
	}

	private static final int getParent(int data[], int key)
	{
	    while (data[key] != key)
	    {
	        key = data[key];
	    }
	    return key;
	}

	private static final void assignParent(int data[], int key, int parent)
    {
        while (data[key] != key)
        {
            int next = data[key];
            data[key] = parent;
            key = next;
        }
        data[key] = parent;
    }

	public static final void collapseShortEdges(PrimitiveList vertexPositions, IntegerArrayList faceIndices, double minDist)
    {
        int vertexTree[] = new int[vertexPositions.size() / 3];
        ArrayUtil.iota(vertexTree);
        int p[] = new int[3];
        int vIndex[] = new int[3];
        for (int f = 0; f < faceIndices.size(); f += 3)
        {
            faceIndices.toArray(vIndex, 0, f, f + 3);
            p[0] = getParent(vertexTree, vIndex[0]); p[1] = getParent(vertexTree, vIndex[1]); p[2] = getParent(vertexTree, vIndex[2]);
            for (int i = 0; i < 3; ++i)
            {
                int j = (i + 1) % 3;
                if (p[i] != p[j])
                {
                    double x0 = vertexPositions.getD(vIndex[i] * 3), y0 = vertexPositions.getD(vIndex[i] * 3 + 1), z0 = vertexPositions.getD(vIndex[i] * 3 + 2);
                    double x1 = vertexPositions.getD(vIndex[j] * 3), y1 = vertexPositions.getD(vIndex[j] * 3 + 1), z1 = vertexPositions.getD(vIndex[j] * 3 + 2);
                    //Difference Vectors
                    double xu = x1 - x0, yu = y1 - y0, zu = z1 - z0;
                    if (xu * xu + yu * yu + zu * zu < minDist)
                    {
                        if (p[i] < p[j]){assignParent(vertexTree, vIndex[j], p[i]);p[j] = p[i];}
                        else            {assignParent(vertexTree, vIndex[i], p[j]);p[i] = p[j];}
                    }
                }
            }
        }
        int count[] = new int[vertexTree.length];
        int oldToNewVertex[] = new int[vertexTree.length];
        int vCount = 0;
        for (int v = 0; v< vertexTree.length; ++v)
        {
            int parent = getParent(vertexTree, v);
            if (parent == v)
            {
                oldToNewVertex[parent] = vCount;
                for (int i = 0; i < 3; ++i)
                {
                    vertexPositions.setElem(vCount * 3 + i, vertexPositions.getD(parent * 3 + i));
                }
                ++vCount;
            }
            else
            {
                for (int i = 0; i < 3; ++i)
                {
                    vertexPositions.setElem(oldToNewVertex[parent] * 3 + i, vertexPositions.getD(oldToNewVertex[parent] * 3 + i) + vertexPositions.getD(v * 3 + i));
                }
            }
            ++count[oldToNewVertex[parent]];
        }
        vertexPositions.setSize(vCount * 3);
        for (int v = 0; v< vCount; ++v)
        {
            if (count[v] > 1)
            {
                for (int i = 0; i < 3; ++i)
                {
                    vertexPositions.setElem(v * 3 + i, vertexPositions.getD(v * 3 + i) / count[v]);
                }
            }
        }
        int fCount = 0;
        for (int f = 0; f < faceIndices.size(); f += 3)
        {
            faceIndices.toArray(vIndex, 0, f, f + 3);
            p[0] = getParent(vertexTree, vIndex[0]); p[1] = getParent(vertexTree, vIndex[1]); p[2] = getParent(vertexTree, vIndex[2]);
            if (p[0] != p[1] && p[1] != p[2] && p[2] != p[0])
            {
                for (int i = 0; i < 3; ++i)
                {
                    faceIndices.setElem(fCount * 3 + i, oldToNewVertex[p[i]]);
                }
                ++fCount;
            }
        }
        faceIndices.setSize(fCount * 3);
    }

	public static void checkMesh(PrimitiveList vertices, IntegerArrayList faces)
	{
        if (vertices.size() % 3 != 0) {throw new RuntimeException("Number of vertices must be multiple of 3");}
        if (faces.size() % 3 != 0) {throw new RuntimeException("Number of faces must be multiple of 3");}
        int numVertices = vertices.size() / 3;
	    for(int f = 0; f < faces.size(); f += 3)
	    {
	        int v0 = faces.getI(f), v1 = faces.getI(f + 1), v2 = faces.getI(f + 2);
	        if (v0 == v1 || v1 == v2 || v2 == v0) {throw new RuntimeException("Degenerated face found");}
	        if (v0 >= numVertices || v0 < 0 || v1 >= numVertices || v1 < 0 || v2 >= numVertices || v2 < 0) {throw new RuntimeException("Vertex index (" + v0 + ' ' + v1 + ' ' + v2  + ") out of bounds 0 to " + numVertices);}
	    }
	}

	private static final void volumeToMeshSlice(
	        int height,
	        int width,
	        int z,
	        int offsets[],
            IntegerArrayList searchStack,
            int vertexIndices[],
            PrimitiveList vertexPositions,
            IntegerArrayList faceIndices,
	        boolean inside[],
	        boolean insideConnected[],
	        boolean visited[],
	        float dataValues[],
            int indexOffset,
	        double mid) {
	    for (int y = 0; y < height - 1; ++y)
        {
            for (int x = 0; x < width - 1; ++x)
            {
                final int index = x + width * y + indexOffset;
                boolean is_cutted = false;
                {
                    boolean first = inside[0] = dataValues[index] > mid;
                    for (int bitmask = 1; bitmask < 8; ++bitmask)
                    {
                        is_cutted |= (inside[bitmask] = (dataValues[index + offsets[bitmask]] > mid)) != first;
                    }
                }
                if (is_cutted)
                {
                    Arrays.fill(visited, false);
                    for (int bitmask = 0; bitmask < 8; ++bitmask)
                    {
                        Arrays.fill(insideConnected, false);
                        int innerVertex = bitmask;
                        int inOutAxis = -1;
                        if (inside[bitmask] && !visited[bitmask])
                        {
                            visited[bitmask] = insideConnected[bitmask] = true;
                            searchStack.add(bitmask);
                            while(!searchStack.isEmpty())
                            {
                                int current = searchStack.pop();
                                for (int axis = 0; axis < 3;++axis)
                                {
                                    int neighbour = current ^ (1 << axis);
                                    if (inside[neighbour] && !visited[neighbour])
                                    {
                                        searchStack.add(neighbour);
                                        visited[neighbour] = insideConnected[neighbour] = true;
                                    }
                                    if (!inside[neighbour])
                                    {
                                        innerVertex = current;
                                        inOutAxis = axis;
                                    }
                                }
                            }
                        }
                        if (inOutAxis == -1){continue;}
                        //At this point connectedSet marks a set of vertices which are connected and are greater then mid
                        int facePoint0 = -1, facePoint1 = -1;
                        final int startInnerVertex = innerVertex;
                        final int startInOutAxis = inOutAxis;
                        while(true)
                        {
                            final int outerVertex = innerVertex ^ (1 << inOutAxis);
                            final int lowerVertex = innerVertex & outerVertex;
                            final int higherVertex = innerVertex | outerVertex;
                            final int vIndex = (x + width * y + offsets[lowerVertex]) * 3 + inOutAxis;
                            int facePoint2 = vertexIndices[vIndex];
                            if (facePoint2 == -1)
                            {
                                final double v0 = dataValues[index + offsets[lowerVertex]], v1 = dataValues[index + offsets[higherVertex]];
                                final double alpha = (v0 - mid) / (v0 - v1);
                                if (alpha < 0 || alpha > 1){throw new RuntimeException("alpha not in range: " + alpha);}
                                /*if (alpha <= slack) //clip near to lattice values to remove small triangles
                                {
                                    vIndex = (cubeIndex + offsets[lowerVertex]) * 4;
                                    facePoint2 = vertexIndices[vIndex];
                                }
                                if (alpha >= 1 - slack) //clip near to lattice values to remove small triangles
                                {
                                    vIndex = (cubeIndex + offsets[higherVertex]) * 4;
                                    facePoint2 = vertexIndices[vIndex];
                                }*/
                                if (facePoint2 == -1)
                                {
                                    facePoint2 = vertexIndices[vIndex] = vertexPositions.size() / 3;
                                    double xp = x + ((lowerVertex >> 0) & 1);
                                    double yp = y + ((lowerVertex >> 1) & 1);
                                    double zp = z + ((lowerVertex >> 2) & 1);
                                    int addIndex = vertexPositions.size() + inOutAxis;
                                    vertexPositions.addTuple(xp, yp, zp);
                                    vertexPositions.setElem(addIndex, vertexPositions.getD(addIndex) + alpha);
                                }
                            }
                            if (facePoint0 == -1)
                            {
                                facePoint0 = facePoint1;
                            }
                            else if (facePoint2 != facePoint1 && facePoint0 != facePoint1 && facePoint0 != facePoint2)
                            {
                               faceIndices.addTuple(facePoint0, facePoint1, facePoint2);
                            }
                            facePoint1 = facePoint2;
                            final int newAxis = (((innerVertex ^ (innerVertex >> 1) ^ (innerVertex >> 2)) & 1) + inOutAxis + 1)%3;
                            final int newInner = innerVertex ^ (1 << newAxis);
                            final int newOuter = outerVertex ^ (1 << newAxis);
                            if (!insideConnected[newInner])
                            {
                                inOutAxis = newAxis;//move on outer vertex
                            }
                            else if (!insideConnected[newOuter])
                            {
                                innerVertex = newInner;//move on both vertices
                            }
                            else
                            {
                                innerVertex = newOuter;//move on inner vertex
                                inOutAxis = newAxis;
                            }
                            if (innerVertex == startInnerVertex && inOutAxis == startInOutAxis)
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }
	}

	public static final void volumeToMesh(float[] data, int width, int height, int depth, double mid, IntegerArrayList faceIndices, PrimitiveList vertexPositions)
    {
        int offsets[] = new int[8];
        int vertexIndices[] = new int[width * height * 2 * 3];
        Arrays.fill(vertexIndices, -1);
        boolean inside[] = new boolean[8];
        boolean visited[] = new boolean[8];
        boolean insideConnected[] = new boolean[8];
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < (1 << i); ++j)
            {
                offsets[j + (1 << i)] = offsets[j] + (i == 0 ? 1 : i == 1 ? width : width * height);
            }
        }
        IntegerArrayList searchStack = new IntegerArrayList();
        for (int z = 0; z < depth - 1; ++z)
        {
            System.arraycopy(vertexIndices, width * height * 3, vertexIndices, 0, width * height * 3);
            Arrays.fill(vertexIndices, width * height * 3, width * height * 6, -1);
            volumeToMeshSlice(height, width, z, offsets, searchStack, vertexIndices, vertexPositions, faceIndices, inside, insideConnected, visited, data, width * height * z, mid);
        }
    }

	public static final void volumeToMesh(DoubleList data, int width, int height, int depth, double mid, IntegerArrayList faceIndices, PrimitiveList vertexPositions)
    {
        int offsets[] = new int[8];
        int vertexIndices[] = new int[width * height * 6];
        Arrays.fill(vertexIndices, -1);
        boolean inside[] = new boolean[8];
        boolean visited[] = new boolean[8];
        boolean insideConnected[] = new boolean[8];
        float dataValues[] = new float[width * height * 2];
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < (1 << i); ++j)
            {
                offsets[j + (1 << i)] = offsets[j] + (i == 0 ? 1 : i == 1 ? width : width * height);
            }
        }
        IntegerArrayList searchStack = new IntegerArrayList();
        if (depth > 0)
        {
            data.toArray(dataValues, width * height, 0, width * height);
        }
        for (int z = 0; z < depth - 1; ++z)
        {
            System.arraycopy(vertexIndices, width * height * 3, vertexIndices, 0, width * height * 3);
            Arrays.fill(vertexIndices, width * height * 3, width * height * 6, -1);

            System.arraycopy(dataValues, width * height, dataValues, 0, width * height);
            data.toArray(dataValues, width * height, (long)width * height * (z + 1), (long)width * height * (z + 2));
            volumeToMeshSlice(height, width, z, offsets, searchStack, vertexIndices, vertexPositions, faceIndices, inside, insideConnected, visited, dataValues, 0, mid);
        }
    }

	/*public void intersectPlaneAndMesh(Vector3d normal, double offset, IntegerArrayList faceIndices, DoubleArrayList vertexPositions, DoubleArrayList intersections)
	{
	    Vector3d v0 = new Vector3d(), v1 = new Vector3d(), v2 = new Vector3d();
	    Vector3d vertices[] = new Vector3d[] {v0,v1,v2};
	    double dot[] = new double[3];
	    Vector3d tmp = new Vector3d();
	    for (int f = 0; f < faceIndices.size(); f += 3)
	    {
	        for (int v = 0; v < 3; ++v)
	        {
	            vertices[v].set(vertexPositions, faceIndices.get(f + v) * 3);
	            dot[v] = normal.dot(vertices[v]);
	        }
            for (int v = 0; v < vertices.length; ++v)
            {
    	        if (dot[v] < offset && dot[(v + 1) % 3] >= offset)
    	        {
    	            tmp.set(vertices[v]);
    	            tmp.add(normal, )
    	            tmp.add(vertices[v]);
    	            intersections.add();
    	        }
            }
	    }
	}*/

	public static final void parse(String str, Vectorf vec) throws ParseException
	{
		if (str.charAt(0) != '(' || str.charAt(str.length() - 1) != ')')
		{
			throw new ParseException(str, 0);
		}
		int begin = 1; int end = str.indexOf(',');
		for (int index = 0; end != -1; ++index)
		{
			vec.setElem(index, StringUtils.parseFloat(str, begin, end));
			begin = end;
			end = str.indexOf(begin, ',');
		}
	}

	public static final void parse(String str, Vectori vec) throws ParseException
	{
		if (str.charAt(0) != '(' || str.charAt(str.length() - 1) != ')')
		{
			throw new ParseException(str, 0);
		}
		int begin = 1; int end = str.indexOf(',');
		for (int index = 0; end != -1; ++index)
		{
			vec.setElem(index, StringUtils.parseInt(str, begin, end, 10));
			begin = end;
			end = str.indexOf(begin, ',');
		}
	}

    public final static void calcFaceNormals (int sizeX, int sizeY, FloatVectorObject vertex, float faceNormal[]){
		if (sizeX * sizeY != vertex.size())                       {throw new ArrayIndexOutOfBoundsException("Vertex Count doesn't match the size: " + sizeX + ',' + sizeY + ") vertexCount: " + vertex.size());}
		if ((sizeX - 1) * (sizeY - 1) != faceNormal.length * 3)   {throw new ArrayIndexOutOfBoundsException("vertexNormal Count doesn't match the size: " + sizeX + ',' + sizeY + ") vertexNormalCount: " + vertex.size());}
		final float vertexX[] = vertex.x, vertexY[] = vertex.y, vertexZ[] = vertex.z;
		int normalIndex = 0;
    	int index00 = 0;
        for (int i=1;i<sizeX;i++){
            for (int j=1;j<sizeY;j++){
            	final int index10 = index00 + sizeX;
            	final int index01 = index00 + 1, index11 = index10 + 1;
                final float ax = vertexX[index11] - vertexX[index00];
                final float ay = vertexY[index11] - vertexY[index00];
                final float az = vertexZ[index11] - vertexZ[index00];

                final float bx = vertexX[index01] - vertexX[index10];
                final float by = vertexY[index01] - vertexY[index10];
                final float bz = vertexZ[index01] - vertexZ[index10];

                final float xn=ay*bz-az*by;
                final float yn=az*bx-ax*bz;
                final float zn=ax*by-ay*bx;
                final float len = 1/(float)Math.sqrt(xn * xn + yn * yn + zn * zn);

                faceNormal[normalIndex]=xn*len;
                faceNormal[normalIndex + 1]=yn*len;
                faceNormal[normalIndex + 2]=zn*len;
                normalIndex += 3;
                ++index00;
            }
            ++index00;
        }
    }

    public final static void calcVertexNormals2(final int sizeX, final int sizeY, FloatVectorObject vertex, FloatVectorObject vertexNormal, boolean xCyclic, boolean yCyclic){
		if (sizeX * sizeY != vertex.size())       {throw new ArrayIndexOutOfBoundsException("Vertex Count doesn't match the size: (" + sizeX + ',' + sizeY + ") vertexCount: " + vertex.size());}
		if (sizeX * sizeY != vertexNormal.size()) {throw new ArrayIndexOutOfBoundsException("vertexNormal Count doesn't match the size: (" + sizeX + ',' + sizeY + ") vertexNormalCount: " + vertex.size());}
    	final float vertexX[] = vertex.x, vertexY[] = vertex.y, vertexZ[] = vertex.z;
		final float vertexNormalX[] = vertexNormal.x, vertexNormalY[] = vertexNormal.y, vertexNormalZ[] = vertexNormal.z;
    	final int sizeXMin1 = sizeX-1, sizeYMin1 = sizeY-1;
    	for (int i=0;i<sizeX;i++){
            final int middleIndexX = i*sizeY;
            final int lowIndexX = i==0 ? (xCyclic ? sizeY * sizeXMin1 : 0) : middleIndexX - sizeY;
            final int highIndexX = i== sizeXMin1 ? (xCyclic ? 0 : middleIndexX) : middleIndexX + sizeY;

    		for (int j=0;j<sizeY;j++){
    			/**
    			 * Die Indices sind folgendermassen verteilt:
    			 * 			i-1		i		i+1
    			 * 	j-1				01
    			 * 	j		10		11		12
    			 * 	j+1				21
    			 * Dabei sind die verschobenen Werte nach oben und unten begrentzt
    			 */
                final int index11 = middleIndexX + j;
                final int index01 = lowIndexX + j, index21 = highIndexX + j;
                final int index10 = middleIndexX + (j==0 ? 0 : j-1);
                final int index12 = middleIndexX + (j==sizeYMin1 ? sizeYMin1 : j+1);

                final float ax = vertexX[index01] - vertexX[index21];
                final float ay = vertexY[index01] - vertexY[index21];
                final float az = vertexZ[index01] - vertexZ[index21];
                final float bx = vertexX[index10] - vertexX[index12];
                final float by = vertexY[index10] - vertexY[index12];
                final float bz = vertexZ[index10] - vertexZ[index12];

                final float xn=ay*bz-az*by;
                final float yn=az*bx-ax*bz;
                final float zn=ax*by-ay*bx;
                final float len = 1/(float)Math.sqrt(xn * xn + yn * yn + zn * zn);

                vertexNormalX[index11]=xn*len;
                vertexNormalY[index11]=yn*len;
                vertexNormalZ[index11]=zn*len;
            }
    	}
    }

    public static final void add(double data[], int begin, int end, int stride, Vectord vec)
    {
    	for(; begin < end; begin += stride)
    	{
    		vec.add(data, begin);
    	}
    }

    public final static void calcQuadMeshVertexNormals(float vertices[], int faces[], float vertexNormals[])
    {
		Arrays.fill(vertexNormals, 0);
    	for (int face = 0; face < faces.length; face += 4)
    	{
            final int index00 = 3 * faces[face];
            final int index01 = 3 * faces[face + 1];
            final int index11 = 3 * faces[face + 2];
            final int index10 = 3 * faces[face + 3];
            final float ax = vertices[index00] - vertices[index11];
            final float ay = vertices[index00 + 1] - vertices[index11 + 1];
            final float az = vertices[index00 + 2] - vertices[index11 + 2];
            final float bx = vertices[index10] - vertices[index01];
            final float by = vertices[index10 + 1] - vertices[index01 + 1];
            final float bz = vertices[index10 + 2] - vertices[index01 + 2];

            final float xn=ay*bz-az*by;
            final float yn=az*bx-ax*bz;
            final float zn=ax*by-ay*bx;
            final float len = 1/(float)Math.sqrt(xn * xn + yn * yn + zn * zn);

            for (int i = face; i < face + 4; ++i)
            {
            	int index = faces[i] * 3;
		        vertexNormals[index] += xn * len;
		        vertexNormals[index + 1] += yn * len;
		        vertexNormals[index + 2] += zn * len;
            }
        }
    	for (int vertex = 0; vertex < vertexNormals.length; vertex += 3)
    	{
    		float xn = vertexNormals[vertex];
    		float yn = vertexNormals[vertex + 1];
    		float zn = vertexNormals[vertex + 2];
    		final float len = 1/(float)Math.sqrt(xn * xn + yn * yn + zn * zn);
    		vertexNormals[vertex] = xn * len;
    		vertexNormals[vertex + 1] = yn * len;
    		vertexNormals[vertex + 2] = zn * len;
    	}
    }

    public static final double crossq(double x1, double y1, double z1, double x2, double y2, double z2)
    {
    	final double x = y1 * z2 - z1 * y2;
    	final double y = z1 * x2 - x1 * z2;
    	final double z = x1 * y2 - y1 * x2;
    	return x * x + y * y + z * z;
    }

    public static final double crossdot(double x1, double y1, double z1, double x2, double y2, double z2, double n0, double n1, double n2)
    {
    	return n0 * (y1 * z2 - z1 * y2) + n1 * (z1 * x2 - x1 * z2) + n2 * (x1 * y2 - y1 * x2);
    }

    public final static void calcTriangleMeshVertexNormals(float vertices[], int faces[], float vertexNormals[])
    {
		Arrays.fill(vertexNormals, 0);
    	for (int face = 0; face < faces.length; face += 3)
    	{
            final int index00 = 3 * faces[face];
            final int index01 = 3 * faces[face + 1];
            final int index10 = 3 * faces[face + 2];
            final float ax = vertices[index10] - vertices[index00];
            final float ay = vertices[index10 + 1] - vertices[index00 + 1];
            final float az = vertices[index10 + 2] - vertices[index00 + 2];
            final float bx = vertices[index01] - vertices[index00];
            final float by = vertices[index01 + 1] - vertices[index00 + 1];
            final float bz = vertices[index01 + 2] - vertices[index00 + 2];

            final float xn=ay*bz-az*by;
            final float yn=az*bx-ax*bz;
            final float zn=ax*by-ay*bx;
            final float len = 1/(float)Math.sqrt(xn * xn + yn * yn + zn * zn);

            for (int i = face; i < face + 3; ++i)
            {
            	int index = faces[i] * 3;
		        vertexNormals[index] += xn * len;
		        vertexNormals[index + 1] += yn * len;
		        vertexNormals[index + 2] += zn * len;
            }
        }
    	for (int vertex = 0; vertex < vertexNormals.length; vertex += 3)
    	{
    		float xn = vertexNormals[vertex];
    		float yn = vertexNormals[vertex + 1];
    		float zn = vertexNormals[vertex + 2];
    		final float len = 1/(float)Math.sqrt(xn * xn + yn * yn + zn * zn);
    		vertexNormals[vertex] = xn * len;
    		vertexNormals[vertex + 1] = yn * len;
    		vertexNormals[vertex + 2] = zn * len;
    	}
    }

    public final static void calcTriangleMeshVertexFaceNormals(double vertices[], int faces[], double vertexNormals[], double faceNormals[])
    {
		Arrays.fill(vertexNormals, 0);
    	for (int face = 0; face < faces.length; face += 3)
    	{
            final int index00 = 3 * faces[face];
            final int index01 = 3 * faces[face + 1];
            final int index10 = 3 * faces[face + 2];
            final double ax = vertices[index10] - vertices[index00];
            final double ay = vertices[index10 + 1] - vertices[index00 + 1];
            final double az = vertices[index10 + 2] - vertices[index00 + 2];
            final double bx = vertices[index01] - vertices[index00];
            final double by = vertices[index01 + 1] - vertices[index00 + 1];
            final double bz = vertices[index01 + 2] - vertices[index00 + 2];

            double xn=ay*bz-az*by;
            double yn=az*bx-ax*bz;
            double zn=ax*by-ay*bx;

            faceNormals[face] = xn;
            faceNormals[face + 1] = yn;
            faceNormals[face + 2] = zn;
            /*final double len = 1/Math.sqrt(xn * xn + yn * yn + zn * zn);
            xn *= len;
            yn *= len;
            zn *= len;
            */
            for (int i = face; i < face + 3; ++i)
            {
            	int index = faces[i] * 3;
		        vertexNormals[index] += xn;
		        vertexNormals[index + 1] += yn;
		        vertexNormals[index + 2] += zn;
            }
        }
    	for (int vertex = 0; vertex < vertexNormals.length; vertex += 3)
    	{
    		double xn = vertexNormals[vertex];
    		double yn = vertexNormals[vertex + 1];
    		double zn = vertexNormals[vertex + 2];
    		final double len = 1/Math.sqrt(xn * xn + yn * yn + zn * zn);
    		vertexNormals[vertex    ] = xn * len;
    		vertexNormals[vertex + 1] = yn * len;
    		vertexNormals[vertex + 2] = zn * len;
    	}
    }

    public final static void calcVertexNormals(final int sizeX, final int sizeY, FloatVectorObject vertex, FloatVectorObject vertexNormal, boolean xCyclic, boolean yCyclic){
		if (sizeX * sizeY != vertex.size())       {throw new ArrayIndexOutOfBoundsException("Vertex Count doesn't match the size: (" + sizeX + ',' + sizeY + ") vertexCount: " + vertex.size());}
		if (sizeX * sizeY != vertexNormal.size()) {throw new ArrayIndexOutOfBoundsException("vertexNormal Count doesn't match the size: (" + sizeX + ',' + sizeY + ") vertexNormalCount: " + vertex.size());}
    	final float vertexX[] = vertex.x, vertexY[] = vertex.y, vertexZ[] = vertex.z;
		final float vertexNormalX[] = vertexNormal.x, vertexNormalY[] = vertexNormal.y, vertexNormalZ[] = vertexNormal.z;
    	final int sizeXMin1 = sizeX-1, sizeYMin1 = sizeY-1;
    	for (int i=0;i<sizeX;i++){
            final int middleIndexX = i*sizeY;
            final int lowIndexX = i==0 ? (xCyclic ? sizeY * sizeXMin1 : 0) : middleIndexX - sizeY;
            final int highIndexX = i== sizeXMin1 ? (xCyclic ? 0 : middleIndexX) : middleIndexX + sizeY;

    		for (int j=0;j<sizeY;j++){
    			/**
    			 * Die Indices sind folgendermassen verteilt:
    			 * 			i-1		i		i+1
    			 * 	j-1				01
    			 * 	j		10		11		12
    			 * 	j+1				21
    			 * Dabei sind die verschobenen Werte nach oben und unten begrentzt
    			 */
                final int index11 = middleIndexX + j;
                final int index01 = lowIndexX + j;
                final int index21 = highIndexX + j;
                final int index10 = index11 + (j==0 ? (yCyclic ? sizeYMin1 : 0)  : -1);
                final int index12 = index11 + (j==sizeYMin1 ? (yCyclic ? -sizeYMin1 : 0) : 1);
                final float ax = vertexX[index01] - vertexX[index21];
                final float ay = vertexY[index01] - vertexY[index21];
                final float az = vertexZ[index01] - vertexZ[index21];
                final float bx = vertexX[index10] - vertexX[index12];
                final float by = vertexY[index10] - vertexY[index12];
                final float bz = vertexZ[index10] - vertexZ[index12];

                final float xn=ay*bz-az*by;
                final float yn=az*bx-ax*bz;
                final float zn=ax*by-ay*bx;
                final float len = 1/(float)Math.sqrt(xn * xn + yn * yn + zn * zn);

                vertexNormalX[index11]=xn*len;
                vertexNormalY[index11]=yn*len;
                vertexNormalZ[index11]=zn*len;
            }
    	}
    }

    public final static void calcVertexNormals(final int sizeX, final int sizeY, FloatVectorObject vertex, float vertexNormal[], boolean xCyclic, boolean yCyclic){
 		if (sizeX * sizeY != vertex.size())          {throw new ArrayIndexOutOfBoundsException("Vertex Count doesn't match the size: (" + sizeX + ',' + sizeY + ") vertexCount: " + vertex.size());}
 		if (sizeX * sizeY * 3 != vertexNormal.length){throw new ArrayIndexOutOfBoundsException("vertexNormal Count doesn't match the size: (" + sizeX + ',' + sizeY + ") vertexNormalCount: " + vertex.size());}

     	final float vertexX[] = vertex.x, vertexY[] = vertex.y, vertexZ[] = vertex.z;
     	final int sizeXMin1 = sizeX-1, sizeYMin1 = sizeY-1;
     	for (int i=0;i<sizeX;i++){
             final int middleIndexX = i*sizeY;
             final int lowIndexX = i==0 ? (xCyclic ? sizeY * sizeXMin1 : 0) : middleIndexX - sizeY;
             final int highIndexX = i== sizeXMin1 ? (xCyclic ? 0 : middleIndexX) : middleIndexX + sizeY;

     		for (int j=0;j<sizeY;j++){
     			/**
     			 * Die Indices sind folgendermassen verteilt:
     			 * 			i-1		i		i+1
     			 * 	j-1				01
     			 * 	j		10		11		12
     			 * 	j+1				21
     			 * Dabei sind die verschobenen Werte nach oben und unten begrentzt
     			 */
                 final int index11 = middleIndexX + j;
                 final int index01 = lowIndexX + j;
                 final int index21 = highIndexX + j;
                 final int index10 = index11 + (j==0 ? (yCyclic ? sizeYMin1 : 0)  : -1);
                 final int index12 = index11 + (j==sizeYMin1 ? (yCyclic ? -sizeYMin1 : 0) : 1);
                 final float ax = vertexX[index01] - vertexX[index21];
                 final float ay = vertexY[index01] - vertexY[index21];
                 final float az = vertexZ[index01] - vertexZ[index21];
                 final float bx = vertexX[index10] - vertexX[index12];
                 final float by = vertexY[index10] - vertexY[index12];
                 final float bz = vertexZ[index10] - vertexZ[index12];

                 final float xn=ay*bz-az*by;
                 final float yn=az*bx-ax*bz;
                 final float zn=ax*by-ay*bx;
                 final float len = 1/(float)Math.sqrt(xn * xn + yn * yn + zn * zn);

                 vertexNormal[index11 * 3    ] = xn*len;
                 vertexNormal[index11 * 3 + 1] = yn*len;
                 vertexNormal[index11 * 3 + 2] = zn*len;
             }
     	}
    }

    public final static void calcVertexNormals(final int sizeX, final int sizeY, float vertex[], float vertexNormal[], boolean xCyclic, boolean yCyclic){
 		if (sizeX * sizeY * 3!= vertex.length)           {throw new ArrayIndexOutOfBoundsException("Vertex Count doesn't match the size: (" + sizeX + ',' + sizeY + ") vertexCount: " +vertex.length/ 3);}
 		if (sizeX * sizeY * 3 != vertexNormal.length)    {throw new ArrayIndexOutOfBoundsException("vertexNormal Count doesn't match the size: (" + sizeX + ',' + sizeY + ") vertexNormalCount: " + vertex.length / 3);}
     	final int sizeXMin1 = (sizeX-1) * 3, sizeYMin1 = (sizeY-1) * 3;
     	for (int i=0;i<sizeX * 3;i += 3){
             final int middleIndexX = i*sizeY;
             final int lowIndexX = i==0 ? (xCyclic ? sizeY * sizeXMin1 : 0) : middleIndexX - sizeY * 3;
             final int highIndexX = i== sizeXMin1 ? (xCyclic ? 0 : middleIndexX) : middleIndexX + sizeY * 3;
     		 for (int j=0;j<sizeY * 3;j += 3){
     			/**
     			 * Die Indices sind folgendermassen verteilt:
     			 * 			i-1		i		i+1
     			 * 	j-1				01
     			 * 	j		10		11		12
     			 * 	j+1				21
     			 * Dabei sind die verschobenen Werte nach oben und unten begrentzt
     			 */
                 final int index11 = (middleIndexX + j );
                 final int index01 = (lowIndexX + j);
                 final int index21 = (highIndexX + j);
                 final int index10 = index11 + (j==0 ? (yCyclic ? sizeYMin1 : 0)  : -3);
                 final int index12 = index11 + (j==sizeYMin1 ? (yCyclic ? -sizeYMin1 : 0) : 3);
                 final float ax = vertex[index01    ] - vertex[index21];
                 final float ay = vertex[index01 + 1] - vertex[index21 + 1];
                 final float az = vertex[index01 + 2] - vertex[index21 + 2];
                 final float bx = vertex[index10    ] - vertex[index12];
                 final float by = vertex[index10 + 1] - vertex[index12 + 1];
                 final float zn=ax*by-ay*bx;
                 final float bz = vertex[index10 + 2] - vertex[index12 + 2];

                 final float xn=ay*bz-az*by;
                 final float yn=az*bx-ax*bz;
                 final float len = 1/(float)Math.sqrt(xn * xn + yn * yn + zn * zn);

                 vertexNormal[index11    ] = xn*len;
                 vertexNormal[index11 + 1] = yn*len;
                 vertexNormal[index11 + 2] = zn*len;
             }
     	}
    }

    public static boolean cropToRectangle(Vector3d v0, Vector3d v1, Rectangle2D rect)
    {
    	double minX = rect.getMinX() + 1, maxX = rect.getMaxX() - 1;
    	double minY = rect.getMinY() + 1, maxY = rect.getMaxY() - 1;
    	double begin = 0;
    	double end = 0;
    	double diffX = v1.x - v0.x;
    	double diffY = v1.y - v0.y;
    	if (v0.x < minX)       {begin = Math.max(begin,(minX - v0.x) / diffX);}
		else if (v0.x > maxX)  {begin = Math.max(begin,(maxX - v0.x) / diffX);}
		if (v0.y < minY)       {begin = Math.max(begin,(minY - v0.y) / diffY);}
		else if (v0.y > maxY)  {begin = Math.max(begin,(maxY - v0.y) / diffY);}
		if (v1.x < minX)       {end   = Math.max(end,  (v1.x - minX) / diffX);}
		else if (v1.x > maxX)  {end   = Math.max(end,  (v1.x - maxX) / diffX);}
		if (v1.y < minY)       {end   = Math.max(end,  (v1.y - minY) / diffY);}
		else if (v1.y > maxY)  {end   = Math.max(end,  (v1.y - maxY) / diffY);}
		if (begin + end< 1)
		{
			v0.x += diffX * begin;
			v0.y += diffY * begin;
			v1.x -= diffX * end;
			v1.y -= diffY * end;
			return true;
		}
		return false;
    }

    public static boolean cropToRectangle(Vector2d v0, Vector2d v1, Rectangle2D rect)
    {
    	double minX = rect.getMinX() + 1, maxX = rect.getMaxX() - 1;
    	double minY = rect.getMinY() + 1, maxY = rect.getMaxY() - 1;
    	double begin = 0;
    	double end = 0;
    	double diffX = v1.x - v0.x;
    	double diffY = v1.y - v0.y;
    	if (Double.isNaN(diffX) || Double.isNaN(diffY)){return false;}
    	if (v0.x < minX)	  {begin = Math.max(begin,(minX - v0.x) / diffX);}
		else if (v0.x > maxX) {begin = Math.max(begin,(maxX - v0.x) / diffX);}
		if (v0.y < minY)      {begin = Math.max(begin,(minY - v0.y) / diffY);}
		else if (v0.y > maxY) {begin = Math.max(begin,(maxY - v0.y) / diffY);}
		if (v1.x < minX)      {end   = Math.max(end,  (v1.x - minX) / diffX);}
		else if (v1.x > maxX) {end   = Math.max(end,  (v1.x - maxX) / diffX);}
		if (v1.y < minY)      {end   = Math.max(end,  (v1.y - minY) / diffY);}
		else if (v1.y > maxY) {end   = Math.max(end,  (v1.y - maxY) / diffY);}
		if (begin + end< 1)
		{
			v0.x += diffX * begin;
			v0.y += diffY * begin;
			v1.x -= diffX * end;
			v1.y -= diffY * end;
			return true;
		}
		return false;
    }

    public static final void rotQuatToMatrix(Vector4d vec, Matrix3d mat)
    {
    	double s = 2 / vec.dot();
    	double vy = s * vec.y, vz = s * vec.z, vw = s * vec.w;
    	mat.m00 = 1 - vy * vec.y;
    	mat.m11 = 1 - vz * vec.z;
    	mat.m22 = 1 - vw * vec.w;
    	double syz = vec.y * vz, syw = vec.y * vw, szw = vec.z * vw;
    	double axw = vec.x * vw, axz = vec.x * vz, axy = vec.x * vy;

    	mat.m01 = syz - axw;
    	mat.m02 = syw + axz;
    	mat.m12 = szw - axy;
    	mat.m01 = syz + axw;
    	mat.m02 = syw - axz;
    	mat.m12 = szw + axy;
    }

    public static final void rotMatrixToQuat(Matrix3d mat, Vector4d vec) {}

    public static final void getOrthorgonalVectors(Vector3d in, Vector3d n0, Vector3d n1)
    {
		Geometry.getOrthorgonalVector(in, n0);
		double length = Math.sqrt(in.dot());
		n0.setNorm(length);
		n1.cross(in, n0);
		n1.setNorm(length);
    }

    public static double[] getVarianceOnSphere(float[] position, float[] direction, byte[] accepted, Vector3d linePosition, Vector3d lineDirection, double evaluation_points[], double[] result)
    {
		int num_vectors = position.length / 3;
		double divide = 1. / ArrayUtil.countNonzero(accepted);
    	double dotprod[] = new double[2 * num_vectors];
       	for (int i = 0; i < num_vectors ; ++i)
    	{
    		if (accepted[i] != 0)
    		{
	    		double xp = position[i * 3] - linePosition.x;
	    		double yp = position[i * 3 + 1] - linePosition.y;
	    		double zp = position[i * 3 + 2] - linePosition.z;
    			dotprod[i * 2] = xp * xp + yp * yp + zp * zp; //c = direction[i * 2] - planeScalar
    			dotprod[i * 2 + 1] = direction[i * 3] * xp + direction[i * 3 + 1] * yp + direction[i * 3 + 2] * zp; //b = dotprod[i * 2 + 1]
    		}
    	}
       	//System.out.println("dotprod:" + Arrays.toString(dotprod));

       	for (int j = 0; j < result.length; ++j)
    	{
    		double variance = 0;
    		double planeScalar = evaluation_points[j];
    		planeScalar *= planeScalar;
    		//System.out.print("alpha " + i + ":");
    		for (int i = 0; i < num_vectors; ++i)
    		{
     			if (accepted[i] != 0)
    			{
     				double b = dotprod[i * 2 + 1];
     				double c = dotprod[i * 2] - planeScalar;
					double alpha = -b + Math.sqrt(b * b - c);
					double xn = position[i * 3]     - linePosition.x + direction[i * 3]     * alpha;
					double yn = position[i * 3 + 1] - linePosition.y + direction[i * 3 + 1] * alpha;
					double zn = position[i * 3 + 2] - linePosition.z + direction[i * 3 + 2] * alpha;
					//System.out.print((lineDirection.dot(xn, yn, zn) / Math.sqrt(planeScalar)) + " ");
					//System.out.print(alpha + " ");

					double arc = Math.acos(Math.min(lineDirection.dot(xn, yn, zn) / evaluation_points[j],1));
					if (Double.isNaN(arc))
					{
						System.out.println(lineDirection.dot(xn, yn, zn) / evaluation_points[j]);
						System.out.println(j + " " + i);
					}
					variance += arc * arc;
    			}
			}
    		//System.out.println();
    		result[j] = variance * divide;
    	}
    	return result;
    }

	public static double[] getVariance(float[] position, float[] direction, byte[] accepted, Vector3d linePosition, Vector3d lineDirection, double[] distances, double[] result) {
		/*java("geometry.Geometry","","getVariance",{{0,0,0},{1,0,0},{1},{0,0,0},{1,0,0},0,1,{0,0,0}})*/
		System.out.println("position:" + Arrays.toString(position));
		System.out.println("direction:" + Arrays.toString(direction));
		System.out.println("accepted:" + Arrays.toString(accepted));
		int num_vectors = position.length / 3;
		double divide = 1. / ArrayUtil.countNonzero(accepted);
    	double dotprod[] = new double[2 * num_vectors];
    	for (int i = 0; i < num_vectors ; ++i)
    	{
    		if (accepted[i] != 0)
    		{
	    		double xp = linePosition.x - position[i * 3];
	    		double yp = linePosition.y - position[i * 3 + 1];
	    		double zp = linePosition.z - position[i * 3 + 2];
	    		dotprod[i * 2]     = lineDirection.dot(xp, yp, zp);
	    		dotprod[i * 2 + 1] = lineDirection.dot(direction, i * 3);
    		}
    	}
    	System.out.println("dotprod:" + Arrays.toString(dotprod));
    	for (int j = 0; j < result.length; ++j)
    	{
    		double variance = 0;
    		double planeScalar = distances[j];
    		for (int i = 0; i < num_vectors; ++i)
    		{
    			if (accepted[i] != 0)
    			{
	    			double t = (dotprod[i * 2] + planeScalar) / dotprod[i * 2 + 1];
	    			double xDiff = t * direction[i * 3]     + position[i * 3]     - linePosition.x - lineDirection.x * planeScalar;
	    			double yDiff = t * direction[i * 3 + 1] + position[i * 3 + 1] - linePosition.y - lineDirection.y * planeScalar;
	    			double zDiff = t * direction[i * 3 + 2] + position[i * 3 + 2] - linePosition.z - lineDirection.z * planeScalar;
	    			variance += xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
    			}
    		}
    		result[j] = variance * divide;
    	}
    	return result;
	}

    public static double[] getVariance(double position[], double direction[], Vector3d linePosition, Vector3d lineDirection, double begin, double end, double result[])
    {
    	int num_vectors = position.length / 3;
    	double dotprod[] = new double[2 * num_vectors];
    	double divide = 1. / num_vectors;
    	for (int i = 0; i < num_vectors ; ++i)
    	{
    		double xp = position[i * 3] - linePosition.x;
    		double yp = position[i * 3] - linePosition.y;
    		double zp = position[i * 3] - linePosition.z;
    		dotprod[i * 2] = lineDirection.dot(xp, yp, zp);
    		dotprod[i * 2 + 1] = lineDirection.dot(direction, i * 3);
    	}
    	for (int i = 0; i < result.length; ++i)
    	{
    		double variance = 0;
    		double planeScalar = begin + i * (end - begin);
    		for (int j = 0; j < num_vectors; ++j)
    		{
    			double t = (dotprod[j * 2] - planeScalar) / dotprod[j * 2 + 1];
    			double xDiff = t * direction[j * 3]     + position[j * 3]     - linePosition.x;
    			double yDiff = t * direction[j * 3 + 1] + position[j * 3 + 1] - linePosition.y;
    			double zDiff = t * direction[j * 3 + 2] + position[j * 3 + 2] - linePosition.z;
    			variance += xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
    		}
    		result[i] = variance * divide;
    	}
    	return result;
    }

    public static final void getOrthorgonalZMatrix(Vector3d in, Matrixd<?> out) {
    	double dirlength = Math.sqrt(in.dot());
    	double qx = in.x * in.x;
		double qy = in.y * in.y;
		double qz = in.z * in.z;

		double lqx = qy + qz;
		double lqy = qx + qz;
		double lqz = qx + qy;
		double n0x, n0y, n0z;

		if (lqx >= lqy && lqx >= lqz)         {n0x = 0;     n0y = -in.z; n0z = in.y;}
		else if (lqy >= lqx && lqy >= lqz)    {n0x = -in.z; n0y = 0;     n0z = in.x;}
		else                                  {n0x = -in.y; n0y = in.x;  n0z = 0;}
		double mult = dirlength / Math.sqrt(n0x * n0x + n0y * n0y + n0z * n0z);
		n0x *= mult; n0y *= mult; n0z *= mult;
		double n1x = in.y * n0z - in.z * n0y;
    	double n1y = in.z * n0x - in.x * n0z;
    	double n1z = in.x * n0y - in.y * n0x;
    	mult = dirlength / Math.sqrt(n1x * n1x + n1y * n1y + n1z * n1z);
    	n1x *= mult; n1y *= mult; n1z *= mult;
    	if (out instanceof Matrix3d)
    	{
    		((Matrix3d)out).setRowMajor(n0x, n1x, in.x, n0y, n1y, in.y, n0z, n1z, in.z);
    		//((Matrix3d)out).set(n0x, n0y, n0z, n1x, n1y, n1z, in.x, in.y, in.z);
    	}
    	if (out instanceof Matrix4d)
    	{
    		((Matrix4d)out).setRowMajor(n0x, n1x, in.x, n0y, n1y, in.y, n0z, n1z, in.z);
    		//((Matrix4d)out).set(n0x, n0y, n0z, n1x, n1y, n1z, in.x, in.y, in.z);
    	}
    }

    public static final void getOrthorgonalVector(Vector3d in, Vector3d out) {
    	double qx = in.x * in.x;
		double qy = in.y * in.y;
		double qz = in.z * in.z;

		double lqx = qy + qz;
		double lqy = qx + qz;
		double lqz = qx + qy;

		if (lqx >= lqy && lqx >= lqz){out.set(0, -in.z, in.y);return;}
		if (lqy >= lqx && lqy >= lqz){out.set(-in.z, 0, in.x);return;}
		out.set(-in.y, in.x,0);
		return;
    }


    public static final double distanceQ(double[] data0, int data0begin, double[] data1, int data1begin, int count) {
        double res = 0;
        for (int i = 0; i < count; ++i)
        {
            final double diff = data0[data0begin + i] - data1[data1begin + i];
            res += diff * diff;
        }
        return res;
    }

	public static final float distanceQ(float[] data0, int data0begin, float[] data1, int data1begin, int count) {
		float res = 0;
		for (int i = 0; i < count; ++i)
		{
			final float diff = data0[data0begin + i] - data1[data1begin + i];
			res += diff * diff;
		}
		return res;
	}

	public static final void getRotationFromTo(Vector3d from, Vector3d to, Matrix3d m)
	{
		double cos = 1 / from.dot();
		double scale = to.dot() * cos;
		cos *= from.dot(to);
		double sin = Math.sqrt(scale - cos * cos);
		/*Crossproduct*/
		double nx = from.y * to.z - from.z * to.y;
		double ny = from.z * to.x - from.x * to.z;
		double nz = from.x * to.y - from.y * to.x;
		double len = 1 / Math.sqrt(nx * nx + ny * ny + nz * nz);
		nx *= len; ny *= len; nz *= len;

		double onemincos = Math.sqrt(scale) - cos;
		m.m00 = (nx * nx * onemincos + cos);
		m.m11 = (ny * ny * onemincos + cos);
		m.m22 = (nz * nz * onemincos + cos);
		/*Symmetric part*/
		double sx = ny * nz * onemincos;
		double sy = nx * nz * onemincos;
		double sz = nx * ny * onemincos;
		/*Asymmetric part*/
		double asx = nx * sin;
		double asy = ny * sin;
		double asz = nz * sin;
		m.m01 = sz + asz;
		m.m10 = sz - asz;
		m.m02 = sy - asy;
		m.m20 = sy + asy;
		m.m12 = sx - asx;
		m.m21 = sx + asx;
	}

	public static void toCart(Vector3d vec, double azimuth, double elevation, double distance) {
		vec.x = distance * Math.sin(elevation);
		double cos = distance * Math.cos(elevation);
		vec.y = cos * Math.sin(azimuth);
		vec.z = cos * Math.cos(azimuth);
	}
}