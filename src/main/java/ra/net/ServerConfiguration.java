package ra.net;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Default configuration.
 *
 * @author Ray Li
 */
public class ServerConfiguration {
  private Properties properties;

  /**
   * Load application.properties.
   *
   * @throws IOException Throws exception when Load configuration file.
   */
  public ServerConfiguration(String path) throws IOException {
    this(Paths.get(path));
  }

  /**
   * Load application.properties.
   *
   * @throws IOException Throws exception when Load configuration file.
   */
  public ServerConfiguration(Path path) throws IOException {
    properties = new Properties();

    if (path != null) {
      properties.load(Files.newBufferedReader(path));
    }
  }

  /**
   * Gets value that specifies key from application.properties.
   *
   * @param key key
   */
  public String getProperty(String key) {
    return properties == null ? null : properties.getProperty(key);
  }

  /**
   * Gets value that specifies key from application.properties.
   *
   * @param key key
   * @param defaultValue value
   */
  public String getProperty(String key, String defaultValue) {
    return properties == null ? null : properties.getProperty(key, defaultValue);
  }

  /**
   * Gets value that specifies key from application.properties.
   *
   * @param key key
   * @return The value converts to integer.
   */
  public int getPropertyAsInt(String key) {
    String result = properties.getProperty(key, null);

    try {
      return Integer.parseInt(result);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Configuration no put key '" + key + "'.");
    }
  }

  /**
   * Gets value that specifies key from application.properties.
   *
   * @param key key
   * @param defaultValue value
   */
  public int getPropertyAsInt(String key, int defaultValue) {
    String result = properties.getProperty(key, null);

    try {
      return result == null ? defaultValue : Integer.parseInt(result);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Configuration no put key '" + key + "'.");
    }
  }

  /** Get all properties. */
  public Properties getProperties() {
    return properties;
  }
}
