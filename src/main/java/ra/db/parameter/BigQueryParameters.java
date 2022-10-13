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

  @Override
  public String getDatabaseUrl() {
    Properties properties = dbProperties;
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

  /**
   * Clone a new builder with the current MysqlParameters state.
   *
   * @return Builder
   */
  public Builder toBuilder() {
    Builder builder =
        new Builder()
            .setHost(dbHost)
            .setOauthType(oauthType)
            .setProjectId(projectId)
            .setPort(dbPort);

    if (dbProperties != null) {
      dbProperties
          .entrySet()
          .forEach(e -> builder.setProperties((String) e.getKey(), (String) e.getValue()));
    }

    return builder;
  }

  public static Builder newBuilder(String projectId, Integer oauthType) {
    return new Builder().setProjectId(projectId).setOauthType(oauthType);
  }

  /** Builder. */
  public static final class Builder {
    private String dbHost;
    private Integer oauthType;
    private String projectId;
    private Integer dbPort;
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
      getProperties().setProperty("OAuthServiceAcctEmail", email);
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
      getProperties().setProperty("OAuthPvtKeyPath", path);
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
     *   ．Properties are case-sensitive.
     *   ．Do not duplicate properties in the connection URL.
     * </pre>
     *
     * @param key key
     * @param value value
     * @return Builder
     */
    public Builder setProperties(String key, String value) {
      getProperties().setProperty(key, value);
      return this;
    }

    /**
     * Returns DbSettings.
     *
     * @return DbSettings
     */
    private Properties getProperties() {
      if (dbProperties == null) {
        dbProperties = new Properties();
      }
      return dbProperties;
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
