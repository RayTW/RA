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
  public R apply(T t, U u, O o);
}
