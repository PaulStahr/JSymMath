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
package maths.exception;


import java.util.List;

import maths.Operation;
import maths.variable.VariableAmount;


/** 
* @author  Paul Stahr
* @version 04.02.2012
*/
public class ExceptionOperation extends Operation
{
	protected final String message;
	
	public ExceptionOperation(String message){
		if ((this.message = message) == null)
			throw new NullPointerException();
	}
	
	
	@Override
	public Operation calculate(VariableAmount object, CalculationController control) {
		return this;
	}

	@Override
	public final int size() {
		return 0;
	}

	
	@Override
	public final Operation get(int index) {
		throw new ArrayIndexOutOfBoundsException(index);
	}
    
	
	@Override
	public final StringBuilder toString(Print type, StringBuilder stringBuilder){
		return stringBuilder.append("Exception:\"").append(message).append('"');
	}
	

	@Override
	public Operation getInstance(List<Operation> subclasses) {
		return this;
	}
}
