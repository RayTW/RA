package ra.util;

/**
 * Ternary Function.
 *
 * @author Ray Li
 * @param <T> T - the type of the first argument to the function
 * @param <U> U - the type of the second argument to the function
 * @param <O> O - the type of the third argument to the function
 * @param <R> R - the type of the result of the function
 */
public interface TernaryFunction<T, U, O, R> {

  /**
   * Ternary function.
   *
   * @param t - the type of the first argument to the function
   * @param u - the type of the second argument to the function
   * @param o - the type of the third argument to the function
   * @return r - the type of the result of the function
   */
  public R apply(T t, U u, O o);
}
