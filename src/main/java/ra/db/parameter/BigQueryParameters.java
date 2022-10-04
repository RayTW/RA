package ra.db.parameter;

import java.util.Properties;
import java.util.stream.Collectors;
import ra.db.DatabaseCategory;

/**
 * BigQuery database parameters.
 *
 * @author Ray Li
 */
public class BigQueryParameters implements DatabaseParameters {
  private String dbHost = "https://www.googleapis.com/bigquery/v2";
  private Integer dbPort = 443;
  private Integer oauthType;
  private String projectId;

  private Properties dbProperties;

  /** Initialize. */
  private BigQueryParameters() {}

  @Override
  public String getHost() {
    return dbHost;
  }

  @Override
  public DatabaseCategory getCategory() {
    return DatabaseCategory.BIGQUERY;
  }

  @Override
  public String getUrlSchema() {
    return DatabaseCategory.BIGQUERY.getSchema();
  }

  @Override
  public String getDriver() {
    return DatabaseCategory.BIGQUERY.getDriver();
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
      queryString =
          properties
              .entrySet()
              .stream()
              .filter(element -> !"ProjectId".equalsIgnoreCase((String) element.getKey()))
              .filter(element -> !"OAuthType".equalsIgnoreCase((String) element.getKey()))
              .map(element -> element.getKey() + "=" + element.getValue())
              .sorted()
              .collect(Collectors.joining(";"));

      if (queryString.length() > 0) {
        queryString = queryString.concat(";");
      }
    }
    String url =
        String.format(
            "%s//%s:%d;ProjectId=%s;OAuthType=%d;%s",
            getUrlSchema(), dbHost, dbPort, projectId, oauthType, queryString);

    return url;
  }

  public static Builder newBuilder(String projectId, Integer oauthType) {
    return new Builder().setProjectId(projectId).setOauthType(oauthType);
  }

  /** MysqlParameters. */
  public static final class Builder {
    private String dbHost;
    private Integer oauthType;
    private String projectId;
    private Integer dbPort;
    private Properties dbProperties = new Properties();

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
     * [Port] is the number of the TCP port to connect to. Specifying the port number is optional if
     * you are connecting to port 443.
     *
     * @param port port
     * @return Builder
     */
    public Builder setPort(Integer port) {
      dbPort = port;
      return this;
    }

    /**
     * [Project] is the name of your BigQuery project.
     *
     * @param projectId project id
     * @return Builder
     */
    public Builder setProjectId(String projectId) {
      this.projectId = projectId;
      return this;
    }

    /**
     * [AuthValue]is a number that specifies the type of authentication used by the connector.
     *
     * <pre>
     * OAuthType:
     * 0 : Using a Google Service Account
     * 1 : Using a Google User Account
     * 2 : Using Pre-Generated Access and Refresh Tokens
     * 3 : Using Application Default Credentials
     * </pre>
     *
     * @param oauthType OAuth Type
     * @return Builder
     */
    public Builder setOauthType(Integer oauthType) {
      this.oauthType = oauthType;
      return this;
    }

    /**
     * Set the OAuthServiceAcctEmail property to your Google service account email address.
     *
     * @param email email
     * @return Builder
     */
    public Builder setOauthServiceAcctEmail(String email) {
      dbProperties.setProperty("OAuthServiceAcctEmail", email);
      return this;
    }

    /**
     * Set the OAuthPvtKeyPath property to the full path to the key file that is used to
     * authenticate the service account email address. This parameter supports keys in .pl2 or .json
     * format.
     *
     * @param path path
     * @return Builder
     */
    public Builder setOauthPvtKeyFile(String path) {
      dbProperties.setProperty("OAuthPvtKeyPath", path);
      return this;
    }

    /**
     * Ïß Set connection setting and DbSettings.
     *
     * <pre>
     * Google BigQuery JDBC Connector Install and Configuration Guide
     * https://storage.googleapis.com/simba-bq-release/jdbc/Simba%20Google%20BigQuery%20JDBC%20Connector%20Install%20and%20Configuration%20Guide.pdf
     * </pre>
     *
     * <pre>
     * Important:
     *   ．Propertiesarecase-sensitive.
     *   ．DonotduplicatepropertiesintheconnectionURL.
     * </pre>
     *
     * @param key key
     * @param value value
     * @return Builder
     */
    public Builder setProperties(String key, String value) {
      dbProperties.setProperty(key, value);
      return this;
    }

    /**
     * Build H2Parameters.
     *
     * @return MysqlParameters
     */
    public BigQueryParameters build() {
      BigQueryParameters param = new BigQueryParameters();

      if (projectId == null) {
        throw new IllegalArgumentException("ProjectId is required.");
      }
      param.projectId = projectId;

      if (oauthType == null) {
        throw new IllegalArgumentException("OAuthType is required.");
      }
      param.oauthType = oauthType;

      if (dbHost != null) {
        param.dbHost = dbHost;
      }

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
  public String toString() {
    return getDatabaseUrl();
  }
}