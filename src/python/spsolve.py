import cupy as cp
import sys
import numpy as np
from cupyx.scipy.sparse import linalg
from cupyx.scipy.sparse import csr_matrix as csr_matrix_cupy
from scipy.sparse import csr_matrix


matValues = np.zeros(shape=int(sys.stdin.readline()),dtype=np.float32)
for i in range(matValues.shape[0]):
    matValues[i] = float(sys.stdin.readline())
colIndices = np.zeros(shape=int(sys.stdin.readline()), dtype=np.uint32)
for i in range(colIndices.shape[0]):
    colIndices[i] = int(sys.stdin.readline())
rowElements = np.zeros(shape=int(sys.stdin.readline()), dtype=np.uint32)
for i in range(rowElements.shape[0]):
    rowElements[i] = int(sys.stdin.readline())
b = np.zeros(shape=int(sys.stdin.readline()), dtype=np.float32)
for i in range(b.shape[0]):
    b[i] = float(sys.stdin.readline())

A = csr_matrix_cupy(csr_matrix((matValues, colIndices, rowElements)))
b = cp.asarray(b)
res = linalg.spsolve(A, b)
res = res.get()

for i in range(len(res)):
    print(res[i])