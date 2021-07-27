package ra.util.parser;

import java.util.function.Function;

/**
 * Configuration loader.
 *
 * @author Ray Li
 */
public interface ConfigParser {

  /**
   * Setting value into the variable which below the Class, after reading the file that is earmarked
   * path.
   *
   * @param clazz plan to be used Class
   * @param path earmarked file path
   */
  public void fill(Class<?> clazz, String path);

  /**
   * Setting value into the variable which below the Class, after reading the file that is earmarked
   * path.
   *
   * @param clazz plan to be used Class
   * @param path earmarked file path
   * @param igonreException Skip reading the variable which was tag true, or throw an Exception when
   *     it´s exists any error and was tag false
   */
  public void fill(Class<?> clazz, String path, boolean igonreException);

  /**
   * Setting value into the variable which below the Class, after reading the file that is earmarked
   * path.
   *
   * @param listener Provider the Class which was earmarked by key word in file
   * @param path earmarked file path
   * @param igonreException Skip reading the variable which was tag true, or throw an Exception when
   *     it´s exists any error and was tag false
   */
  public void fill(Function<String, Class<?>> listener, String path, boolean igonreException);
}
