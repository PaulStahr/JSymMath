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
package maths.functions.hyperbolic;


import java.util.List;

import maths.Operation;
import maths.algorithm.OperationCalculate;
import maths.data.ArrayOperation;
import maths.data.RealDoubleOperation;
import maths.exception.ArrayIndexOutOfBoundsExceptionOperation;
import maths.functions.FunctionOperation;
import maths.variable.VariableAmount;

/** 
* @author  Paul Stahr
* @version 04.02.2012
*/
public final class ArcTangems2Operation extends FunctionOperation
{
    public final Operation a, b;

    public ArcTangems2Operation (Operation operation0, Operation operation1){
        if ((a = operation0) == null || (b = operation1) == null)
            throw new NullPointerException();
    }
    
    public static Operation calculate (final Operation a, final Operation b){
        if (a.isRealFloatingNumber()&&b.isRealFloatingNumber() && !a.isNaN() && !b.isNaN())
            return new RealDoubleOperation(Math.atan2(a.doubleValue(), b.doubleValue()));
        if (a.isArray() && b.isArray()){
            if (a.size() != b.size())
                return new ArrayIndexOutOfBoundsExceptionOperation();
            return new ArrayOperation.ArrayCreator(a.size()){
                
                @Override
                public final Operation get(int index) {
                    return calculate(a.get(index), b.get(index));
                }
            }.getArray();
        }
        if (a.isArray() && b.isRealFloatingNumber()){
            return new ArrayOperation.ArrayCreator(a.size()){
                
                @Override
                public final Operation get(int index) {
                    return calculate(a.get(index), b);
                }
            }.getArray();
        }
        if (a.isRealFloatingNumber() && b.isArray()){
            return new ArrayOperation.ArrayCreator(a.size()){
                
                @Override
                public final Operation get(int index) {
                    return calculate(a, b.get(index));
                }
            }.getArray();
        }
        Operation res;
        if ((res = OperationCalculate.standardCalculations(a, b))!=null)
            return res;
        return new ArcTangems2Operation (a, b);        
    }

    
    @Override
    public Operation calculate (VariableAmount object, CalculationController control){
        return calculate(a.calculate(object, control),b.calculate(object, control));
    }
    
    @Override
    public final int size() {
        return 2;
    }

    
    @Override
    public final Operation get(int index) {
        switch (index){
            case 0: return a;
            case 1: return b;
            default:throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    
    @Override
    public String getFunctionName() {
        return "atan2";
    }

    @Override
    public Operation getInstance(List<Operation> subclasses) {
        return new ArcTangems2Operation(subclasses.get(0), subclasses.get(1));
    }
}
