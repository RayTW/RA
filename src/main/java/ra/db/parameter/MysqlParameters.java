package ra.db.parameter;

import java.util.Properties;
import java.util.function.Supplier;
import ra.db.DatabaseCategory;

/**
 * MySQL database parameters.
 *
 * @author Ray Li
 */
public class MysqlParameters implements DatabaseParameters, Accountable {
  private String dbHost;
  private String dbName;
  private String dbUser;
  private String dbPassword;
  private int dbPort = 3306;
  private static final int SOCKET_TIMEOUT = 60000 * 3;
  private static final int CONNECT_TIMEOUT = 60000 * 3;
  private Properties dbProperties;

  private MysqlParameters() {}

  /**
   * Set database name.
   *
   * @param name database name
   */
  public void setName(String name) {
    dbName = name;
  }

  @Override
  public String getHost() {
    return dbHost;
  }

  /**
   * Returns database name.
   *
   * @return String
   */
  public String getName() {
    return dbName;
  }

  @Override
  public String getUser() {
    return dbUser;
  }

  @Override
  public String getPassword() {
    return dbPassword;
  }

  @Override
  public DatabaseCategory getCategory() {
    return DatabaseCategory.MYSQL;
  }

  @Override
  public String getUrlSchema() {
    return DatabaseCategory.MYSQL.getSchema();
  }

  @Override
  public String getDriver() {
    return DatabaseCategory.MYSQL.getDriver();
  }

  @Override
  public int getPort() {
    return dbPort;
  }

  /**
   * Returns DbSettings.
   *
   * @return DbSettings
   */
  public Properties getProperties() {
    return dbProperties;
  }

  @Override
  public String getDatabaseUrl() {
    Properties properties = getProperties();
    String queryString = "";

    if (properties != null) {
      StringBuilder str = new StringBuilder();

      properties
          .entrySet()
          .stream()
          .forEach(
              obj -> {
                if (str.length() > 0) {
                  str.append("&");
                }
                str.append(obj.getKey());
                str.append("=");
                str.append(obj.getValue());
              });

      queryString = "?" + str.toString();
    }

    return getUrlSchema() + getHost() + ":" + getPort() + "/" + getName() + queryString;
  }

  /** MysqlParameters. */
  public static class Builder {
    private String dbHost;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private Integer dbPort;
    private Boolean profileSql;
    private Properties dbProperties;

    /**
     * Set host of databases.
     *
     * @param host host
     * @return Builder
     */
    public Builder setHost(String host) {
      dbHost = host;
      return this;
    }

    /**
     * Set database name.
     *
     * @param name database name
     * @return Builder
     */
    public Builder setName(String name) {
      dbName = name;
      return this;
    }

    /**
     * Set database user name.
     *
     * @param user user name
     * @return Builder
     */
    public Builder setUser(String user) {
      dbUser = user;
      return this;
    }

    /**
     * Set database user password.
     *
     * @param password user password
     * @return Builder
     */
    public Builder setPassword(String password) {
      dbPassword = password;
      return this;
    }

    /**
     * Set database port.
     *
     * @param port database port
     * @return Builder
     */
    public Builder setPort(Integer port) {
      dbPort = port;
      return this;
    }

    /**
     * Set connection setting and DbSettings.
     *
     * @param supplier all properties
     * @return Builder
     */
    public Builder setProperties(Supplier<Properties> supplier) {
      dbProperties = supplier == null ? null : supplier.get();
      return this;
    }

    /**
     * enable/disable debug mode.
     *
     * @param enable enable/disable debug mode.
     * @return Builder
     */
    public Builder setProfileSql(boolean enable) {
      profileSql = enable;
      return this;
    }

    /**
     * build.
     *
     * @return MysqlParameters
     */
    public MysqlParameters build() {
      MysqlParameters param = new MysqlParameters();

      param.dbHost = dbHost;
      param.dbName = dbName;
      param.dbUser = dbUser;
      param.dbPassword = dbPassword;

      if (dbPort != null) {
        param.dbPort = dbPort.intValue();
      }

      if (profileSql != null) {
        if (dbProperties == null) {
          dbProperties = new Properties();
        }
        dbProperties.put("profileSQL", profileSql.toString());
      }

      if (dbProperties != null && dbProperties.size() > 0) {
        // Keep the preset parameters and then new parameters.
        Properties defaultProperties = new Properties();

        setupDefaultProperty(defaultProperties);

        defaultProperties.putAll(dbProperties);
        param.dbProperties = defaultProperties;
      } else {
        Properties properties = new Properties();

        setupDefaultProperty(properties);
        param.dbProperties = properties;
      }

      return param;
    }

    /**
     * Load default properties before connecting database.
     *
     * @param defaultProperties default properties
     */
    public void setupDefaultProperty(Properties defaultProperties) {
      defaultProperties.put("useUnicode", "true");
      defaultProperties.put("characterEncoding", "utf8");
      defaultProperties.put("socketTimeout", String.valueOf(SOCKET_TIMEOUT));
      defaultProperties.put("connectTimeout", String.valueOf(CONNECT_TIMEOUT));
    }
  }

  @Override
  public String toString() {
    return getHost() + "/" + getName();
  }
}
