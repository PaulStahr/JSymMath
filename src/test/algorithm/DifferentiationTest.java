package test.algorithm;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import maths.Controller;
import maths.Operation;
import maths.OperationCompiler;
import maths.data.BooleanOperation;
import maths.data.RealDoubleOperation;
import maths.data.RealLongOperation;
import maths.exception.OperationParseException;
import maths.functions.AbsoluteOperation;
import maths.functions.DifferentiationOperation;
import maths.functions.atomic.AdditionOperation;
import maths.functions.atomic.DivisionOperation;
import maths.functions.atomic.LowerOperation;
import maths.functions.atomic.MultiplicationOperation;
import maths.functions.atomic.SubtractionOperation;
import maths.variable.UserVariableOperation;
import maths.variable.Variable;
import maths.variable.VariableStack;

@RunWith(Parameterized.class)
public class DifferentiationTest {
    private static class TestInstance
    {
        String op;
        Operation pos;
        private TestInstance(String op, Operation pos)
        {
            this.op = op;
            this.pos = pos;
        }
    }

    private final TestInstance test;

    public DifferentiationTest(TestInstance test)
    {
        this.test = test;
    }

    @Parameters
    public static List<TestInstance> params() {
        return Arrays.asList(
                new TestInstance("x", RealLongOperation.ZERO),
                new TestInstance("x*x", RealLongOperation.ZERO),
                new TestInstance("x^x", RealLongOperation.POSITIVE_TWO),
                new TestInstance("sign(x)", RealLongOperation.POSITIVE_TWO),
                new TestInstance("sign(x)", RealLongOperation.NEGATIVE_ONE),
                new TestInstance("abs(x)", RealLongOperation.POSITIVE_TWO),
                new TestInstance("abs(x)", RealLongOperation.NEGATIVE_ONE),
                new TestInstance("1/x", RealLongOperation.POSITIVE_TWO),
                new TestInstance("log(x)", RealLongOperation.POSITIVE_TWO),
                new TestInstance("sin(x)", RealLongOperation.POSITIVE_TWO),
                new TestInstance("cos(x)", RealLongOperation.POSITIVE_TWO),
                new TestInstance("tan(x)", RealLongOperation.POSITIVE_TWO),
                new TestInstance("sinh(x)", RealLongOperation.POSITIVE_TWO),
                new TestInstance("cosh(x)", RealLongOperation.POSITIVE_TWO),
                new TestInstance("tanh(x)", RealLongOperation.POSITIVE_TWO));
            }

    @Test
    public void testNumeric() throws OperationParseException {
        Operation op = OperationCompiler.compile(test.op);
        Variable v = new Variable("x", test.pos);
        VariableStack vs = new VariableStack();
        vs.add(v);
        Operation value = v.getValue();
        v.setValue((Operation)null);
        Operation.CalculationController control = new Controller();
        Operation exact = DifferentiationOperation.calculate(op, new UserVariableOperation(v.nameObject), control);
        v.setValue(value);
        exact = exact.calculate(vs, control);


        Operation delta = new RealDoubleOperation(0.0001);
        v.setValue(AdditionOperation.calculate(value, delta, control));
        Operation rhs = op.calculate(vs, control);
        v.setValue(SubtractionOperation.calculate(value, delta, control));
        Operation lhs = op.calculate(vs, control);
        Operation approximated = DivisionOperation.calculate(SubtractionOperation.calculate(lhs, rhs, control), MultiplicationOperation.calculate(delta, RealLongOperation.POSITIVE_TWO, control), control);

        assertEquals(approximated.toString(exact.toString(new StringBuilder("Error gap to large: ").append('|')).append('-')).append('|').append('<').append(0.01).toString(), BooleanOperation.TRUE, LowerOperation.calculate(AbsoluteOperation.calculate(SubtractionOperation.calculate(exact, approximated, control)), new RealDoubleOperation(0.01)));
    }
}

