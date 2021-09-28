package maths.functions;

import java.util.List;

import maths.Operation;
import maths.data.ArrayOperation;
import maths.data.RealLongOperation;
import maths.functions.atomic.AdditionOperation;
import maths.functions.atomic.PowerOperation;
import maths.functions.atomic.PowerOperation.SquareRootOperation;
import maths.variable.VariableAmount;

public class NormOperation extends FunctionOperation {
	Operation a;

	@Override
	public String getFunctionName() {
		return "norm";
	}

    public NormOperation (Operation a){
    	if ((this.a = a) == null)
    		throw new NullPointerException();
    }

	@Override
	public Operation getInstance(List<Operation> subclasses) {
		return new NormOperation(subclasses.get(0));
	}

    public static final Operation calculate (final Operation a, CalculationController control){
        if (a instanceof ArrayOperation)
        {
            Operation result = PowerOperation.calculate(a.get(0), RealLongOperation.POSITIVE_TWO, control);
            for (int i = 1; i < a.size(); ++i)
            {
                result = AdditionOperation.calculate(result, PowerOperation.calculate(a.get(i), RealLongOperation.POSITIVE_TWO, control), control);
            }
            return SquareRootOperation.calculate(result);
        }
        return new NormOperation(a);
    }


	@Override
	public Operation calculate(VariableAmount object, CalculationController control) {
		return calculate(a.calculate(object, control), control);
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public Operation get(int index) {
		switch (index){
		case 0: return a;
		default:throw new ArrayIndexOutOfBoundsException(index);
	}
	}

}
