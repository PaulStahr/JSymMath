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
package maths;

import java.util.List;

import maths.data.BooleanOperation;
import maths.functions.FunctionOperation;
import maths.variable.VariableAmount;

/** 
* @author  Paul Stahr
* @version 04.02.2012
*/
public class ProgramFunction extends FunctionOperation {
	public final String name;
	private final Runnable runnable;
	
	public ProgramFunction(String name, Runnable runnable){
		if ((this.name = name) == null)
			throw new NullPointerException();
		this.runnable = runnable;
	}	
	public ProgramFunction(String name){
		if ((this.name = name) == null)
			throw new NullPointerException();
		this.runnable = null;
	}
	
	public void run()
	{
		runnable.run();
	}
	
	@Override
	public final Operation calculate(VariableAmount o, CalculationController control){
		try{
			run();
		}catch(Exception e){
			return BooleanOperation.FALSE;
		}
		return BooleanOperation.TRUE;
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
	public String getFunctionName() {
		return name;
	}
	
	@Override
	public Operation getInstance(List<Operation> subclasses) {
		return this;
	}
}
