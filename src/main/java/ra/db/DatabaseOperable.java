package ra.db;

import java.sql.SQLException;
import ra.exception.RaConnectException;

/**
 * Represents a function that accepts one argument and produces a result.
 *
 * <pre>
 *  T - the type of the input to the function
 *  R - the type of the result of the function
 * </pre>
 *
 * @author Ray Li
 */
public interface DatabaseOperable<T, R> {

  /**
   * Applies this function to the given argument.
   *
   * @param input input
   * @return result
   */
  public R apply(T input) throws RaConnectException, SQLException;
}
