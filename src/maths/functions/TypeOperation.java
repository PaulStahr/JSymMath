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
package maths.functions;


import java.util.List;

import maths.Operation;
import maths.data.ArrayOperation;
import maths.data.BooleanOperation;
import maths.data.CharacterOperation;
import maths.data.ComplexDoubleOperation;
import maths.data.MapOperation;
import maths.data.RealDoubleOperation;
import maths.data.RealLongOperation;
import maths.data.StringOperation;
import maths.variable.VariableAmount;

/**
* @author  Paul Stahr
* @version 04.02.2012
*/
public final class TypeOperation extends FunctionOperation
{
    private Operation a;
    private static final StringOperation result_bool    = new StringOperation("bool");
    private static final StringOperation result_int     = new StringOperation("int");
    private static final StringOperation result_float   = new StringOperation("float");
    private static final StringOperation result_complex = new StringOperation("complex");
    private static final StringOperation result_string  = new StringOperation("string");
    private static final StringOperation result_char    = new StringOperation("char");
    private static final StringOperation result_map     = new StringOperation("map");
    private static final StringOperation result_list    = new StringOperation("list");
    private static final StringOperation result_set     = new StringOperation("set");


    public TypeOperation (Operation a){
    	if ((this.a = a) == null)
    		throw new NullPointerException();
    }

    private Operation calculate(final Operation a){
        if (a.isPrimitive())
        {
            if (a instanceof BooleanOperation)      {return result_bool;}
            if (a instanceof StringOperation)       {return result_string;}
            if (a instanceof RealLongOperation)     {return result_int;}
            if (a instanceof RealDoubleOperation)   {return result_float;}
            if (a instanceof CharacterOperation)    {return result_char;}
            if (a instanceof ComplexDoubleOperation){return result_complex;}
            if (a instanceof MapOperation)          {return result_map;}
            if (a instanceof ArrayOperation)        {return result_list;}
        }
        return new TypeOperation(a);
    }



    @Override
    public final String getFunctionName(){
        return "type";
    }


	@Override
	public Operation calculate (VariableAmount object, CalculationController control){
        return calculate(a.calculate(object, control));
    }

	@Override
	public final int size() {
		return 1;
	}


	@Override
	public final Operation get(int index) {
		switch (index){
			case 0: return a;
			default:throw new ArrayIndexOutOfBoundsException(index);
		}
	}


	@Override
	public final StringBuilder toString(Print type, StringBuilder stringBuilder){
        return a.toString(type, stringBuilder).append('!');
    }

	@Override
	public Operation getInstance(List<Operation> subclasses) {
		return new DeterminantenOperation(subclasses.get(0));
	}
}
