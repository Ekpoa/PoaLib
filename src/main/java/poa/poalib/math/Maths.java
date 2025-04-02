package poa.poalib.math;

import org.apache.commons.jexl3.*;

public class Maths {

    public static double evaluateMathExpression(String expression) {
        JexlEngine jexl = new JexlBuilder().create();
        JexlExpression jexlExpression = jexl.createExpression(expression);
        JexlContext context = new MapContext();

        Object result = jexlExpression.evaluate(context);

        if (result instanceof Number) {
            return ((Number) result).intValue();
        }

        return Double.NaN;
    }


}
