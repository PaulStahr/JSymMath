package test.maths;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import maths.Operation;
import maths.OperationCompiler;
import maths.data.RealLongOperation;
import maths.functions.atomic.LinkingOperation;
import maths.variable.UserVariableOperation;

@RunWith(Parameterized.class)
public class PriorityTest {
    Operation op;
    public PriorityTest(Operation op)
    {
        this.op = op;
    }

    @Parameters
    public static List<Operation> params() {
        final UserVariableOperation v = new UserVariableOperation("foo");
        Operation res[] = new Operation[OperationCompiler.getCalculationCharCount()];
        for (int i = 0; i < res.length; ++i)
        {
            res[i] = OperationCompiler.get(OperationCompiler.getCalculationChar(i), RealLongOperation.ZERO, v);
        }
        return Arrays.asList(res);
    }

    @Test
    public void checkPriority()
    {
        if (!(op instanceof LinkingOperation)){return;}
        int priority = OperationCompiler.getPriority(((LinkingOperation)op).getChar());
        assertEquals(op.getClass().getName() + " had priority " + op.getPriority() + " but compiler assumed " + priority,priority, op.getPriority());
    }
}
