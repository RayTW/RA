package ra.db;

/**
 * database drive category.
 *
 * @author Ray Li
 */
public enum DatabaseCategory {
  /** The driver that MySQL database. */
  MYSQL("jdbc:mysql://", "com.mysql.jdbc.Driver"),
  /** The driver that H2 database. */
  H2("jdbc:h2:", "org.h2.Driver");
  private final String schema;
  private final String driver;

  private DatabaseCategory(String schema, String driver) {
    this.schema = schema;
    this.driver = driver;
  }

  /**
   * driver schema.
   *
   * @return schema
   */
  public String getSchema() {
    return schema;
  }

  /**
   * driver name.
   *
   * @return driver name
   */
  public String getDriver() {
    return driver;
  }
}
