package ra.db.parameter;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Supplier;
import ra.db.DatabaseCategory;

/**
 * MySQL database parameters.
 *
 * @author Ray Li
 */
public class H2Parameters implements DatabaseParameters {
  private String dbHost;
  private String dbName;
  private String dbUser;
  private String dbPassword;
  private String mode;
  private Integer dbPort;
  private Properties dbProperties;

  private H2Parameters() {}

  @Override
  public String getHost() {
    return dbHost;
  }

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
    return DatabaseCategory.H2;
  }

  @Override
  public String getUrlSchema() {
    return DatabaseCategory.H2.getSchema();
  }

  @Override
  public String getDriver() {
    return DatabaseCategory.H2.getDriver();
  }

  @Override
  public int getPort() {
    return dbPort;
  }

  public Properties getProperties() {
    return dbProperties;
  }

  @Override
  public String getDatabaseUrl() {
    Properties properties = getProperties();
    String queryString = "";

    if (properties != null) {
      queryString =
          properties
              .entrySet()
              .stream()
              .map(element -> element.getKey() + "=" + element.getValue())
              .reduce("", (a, b) -> a + ";" + b);
      ;
    }

    String server = "";

    if (dbHost != null) {
      if (dbPort != null) {
        server = String.format("//%s:%d/", dbHost, dbPort);
      } else {
        server = String.format("//%s/", dbHost);
      }
    }
    String url = getUrlSchema() + mode + server + getName() + queryString;

    return url;
  }

  /** MysqlParameters. */
  public static class Builder {
    private String dbHost;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private Integer dbPort;
    private Properties dbProperties;
    private String mode;

    public Builder setHost(String host) {
      dbHost = host;
      return this;
    }

    public Builder setName(String name) {
      dbName = name;
      return this;
    }

    public Builder setUser(String user) {
      dbUser = user;
      return this;
    }

    public Builder setPassword(String password) {
      dbPassword = password;
      return this;
    }

    public Builder setPort(Integer port) {
      dbPort = port;
      return this;
    }

    /**
     * Set connection setting and DbSettings.
     *
     * <pre>
     * https://www.h2database.com/html/features.html
     * https://www.h2database.com/javadoc/org/h2/engine/DbSettings.html
     * </pre>
     *
     * @param supplier all properties
     * @return Builder
     */
    public Builder setProperties(Supplier<Properties> supplier) {
      dbProperties = supplier == null ? null : supplier.get();
      return this;
    }

    /** Embedded (local) connection. */
    public Builder localFile(String path) {
      mode = path + "/";
      return this;
    }

    /** In-memory mode. */
    public Builder inMemory() {
      mode = "mem:";
      return this;
    }

    /** Server mode (remote connections) using TCP/IP. */
    public Builder tcp() {
      mode = "tcp:";
      return this;
    }

    /** Using encrypted. */
    public Builder ssl() {
      mode = "ssl:";
      return this;
    }

    /** Database in a zip file. */
    public Builder zip() {
      mode = "zip:";
      return this;
    }

    /**
     * build.
     *
     * @return MysqlParameters
     */
    public H2Parameters build() {
      if (mode == null) {
        inMemory();
      }

      if (mode.equals("tcp:")) {
        if (dbHost == null) {
          throw new InvalidParameterException("The parameter 'dbHost' are required. ");
        }
      }

      if (dbName == null) {
        dbName = "";
      }

      H2Parameters param = new H2Parameters();

      param.mode = mode;
      param.dbHost = dbHost;
      param.dbName = dbName;
      param.dbUser = dbUser;
      param.dbPassword = dbPassword;

      if (dbPort != null) {
        param.dbPort = dbPort.intValue();
      }

      if (dbProperties != null && dbProperties.size() > 0) {
        param.dbProperties = dbProperties;
      }

      return param;
    }
  }

  @Override
  public void setupConnection(Connection connection) throws SQLException {}

  @Override
  public String toString() {
    return getDatabaseUrl();
  }
}
