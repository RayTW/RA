package ra.util.parser;

import java.util.function.Function;

/**
 * Configuration loader.
 *
 * @author Ray Li
 */
public interface ConfigParser {
  public void fill(Class<?> clazz, String path);

  public void fill(Class<?> clazz, String path, boolean igonreException);

  public void fill(Function<String, Class<?>> listener, String path, boolean igonreException);
}
