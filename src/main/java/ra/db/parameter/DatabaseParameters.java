package ra.db.parameter;

import ra.db.DatabaseCategory;

/**
 * Parameters of Database connect setting.
 *
 * @author Ray Li
 */
public interface DatabaseParameters {
  public DatabaseCategory getCategory();

  /**
   * Returns JDBC schema. Example: "jdbc:mysql:/
   *
   * @return port
   */
  public String getUrlSchema();

  /**
   * Returns JDBC driver.
   *
   * @return port
   */
  public String getDriver();

  /**
   * Returns host that connects to database.
   *
   * @return port
   */
  public String getHost();

  /**
   * Returns port that connects to database.
   *
   * @return port
   */
  public int getPort();

  /**
   * Returns database user.
   *
   * @return user
   */
  public String getUser();

  /**
   * Returns database password.
   *
   * @return password
   */
  public String getPassword();

  /**
   * Returns the URL of parameters.
   *
   * @return URL
   */
  public String getDatabaseUrl();
}
