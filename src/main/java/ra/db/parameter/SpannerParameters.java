package ra.db.parameter;

import java.util.Properties;
import java.util.stream.Collectors;
import ra.db.DatabaseCategory;

/**
 * Google Cloud Spanner parameters.
 *
 * <pre>
 * Connect JDBC to a Google Standard SQL-dialect database
 * https://cloud.google.com/spanner/docs/use-oss-jdbc</pre>
 *
 * @author Ray Li
 */
public class SpannerParameters implements DatabaseParameters {
  private static final String PROJECT = "projectId";
  private static final String INSTANCE = "instanceId";
  private static final String DATABASE = "databaseId";
  private static final String CREDENTIALS = "credentials";

  private Properties dbProperties;

  /** Initialize. */
  private SpannerParameters() {}

  @Override
  public String getHost() {
    return "";
  }

  @Override
  public DatabaseCategory getCategory() {
    return DatabaseCategory.SPANNER;
  }

  @Override
  public String getUrlSchema() {
    return DatabaseCategory.SPANNER.getSchema();
  }

  @Override
  public String getDriver() {
    return DatabaseCategory.SPANNER.getDriver();
  }

  @Override
  public int getPort() {
    return 0;
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
              .filter(element -> !PROJECT.equalsIgnoreCase((String) element.getKey()))
              .filter(element -> !INSTANCE.equalsIgnoreCase((String) element.getKey()))
              .filter(element -> !DATABASE.equalsIgnoreCase((String) element.getKey()))
              .map(element -> element.getKey() + "=" + element.getValue())
              .sorted()
              .collect(Collectors.joining(";"));

      if (queryString.length() > 0) {
        queryString = "?" + queryString + ";";
      }
    }
    String projectId = properties.getProperty(PROJECT);
    String instanceId = properties.getProperty(INSTANCE);
    String databaseId = properties.getProperty(DATABASE);
    String url =
        String.format(
            "%s/projects/%s/instances/%s/databases/%s%s",
            getUrlSchema(), projectId, instanceId, databaseId, queryString);

    return url;
  }

  /**
   * Clone a new builder with the current SpannerParameters state.
   *
   * @return Builder
   */
  public Builder toBuilder() {
    Builder builder = new Builder();

    if (dbProperties != null) {
      dbProperties
          .entrySet()
          .forEach(e -> builder.setProperties((String) e.getKey(), (String) e.getValue()));
    }

    return builder;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  /** Builder. */
  public static final class Builder {
    private Properties dbProperties;

    /**
     * The Cloud Spanner project name.
     *
     * @param projectId project id
     * @return Builder
     */
    public Builder setProjectId(String projectId) {
      getProperties().put(PROJECT, projectId);
      return this;
    }

    /**
     * The Cloud Spanner project instance name.
     *
     * @param instanceId instance id
     * @return Builder
     */
    public Builder setInstanceId(String instanceId) {
      getProperties().put(INSTANCE, instanceId);
      return this;
    }

    /**
     * The Cloud Spanner database name.
     *
     * @param databaseId database id
     * @return Builder
     */
    public Builder setDatabaseId(String databaseId) {
      getProperties().put(DATABASE, databaseId);
      return this;
    }

    /**
     * Set the credentials property to the full path to the key file that is used to authenticate
     * the service account email address. This parameter supports keys in .json format.
     *
     * <p>if the path to your private key file is not specified in the GOOGLE_ APPLICATION_
     * CREDENTIALS environment variable.
     *
     * @param path path
     * @return Builder
     */
    public Builder setCredentials(String path) {
      getProperties().setProperty(CREDENTIALS, path);
      return this;
    }

    /**
     * Ïß Set connection setting and DbSettings.
     *
     * <pre>
     * Google Cloud Spanner JDBC Install and Configuration Guide
     * https://github.com/googleapis/java-spanner-jdbc
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
     * @return SpannerParameters
     */
    public SpannerParameters build() {
      if (dbProperties == null) {
        throw new IllegalArgumentException("Invalid argment");
      }

      if (getProperties().getProperty(PROJECT) == null) {
        throw new IllegalArgumentException("Project is a required argment.");
      }

      if (getProperties().getProperty(INSTANCE) == null) {
        throw new IllegalArgumentException("Instance is a required argment.");
      }

      if (getProperties().getProperty(DATABASE) == null) {
        throw new IllegalArgumentException("Database is a required argment.");
      }

      SpannerParameters param = new SpannerParameters();

      param.dbProperties = dbProperties;

      return param;
    }
  }

  @Override
  public String toString() {
    return getDatabaseUrl();
  }
}
