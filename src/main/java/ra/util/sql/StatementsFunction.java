package ra.util.sql;

import java.lang.reflect.Field;
import ra.util.TernaryFunction;

/**
 * Statements function.
 *
 * @author Ray Li
 */
public interface StatementsFunction
    extends TernaryFunction<Field, StringBuilder, Object, Boolean> {}
