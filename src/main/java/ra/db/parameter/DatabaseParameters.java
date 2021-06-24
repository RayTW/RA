package ra.db.parameter;

import java.sql.Connection;
import java.sql.SQLException;
import ra.db.DatabaseCategory;

/**
 * Parameters of Database connect setting.
 *
 * @author Ray Li
 */
public interface DatabaseParameters {
  public DatabaseCategory getCategory();

  public String getUrlSchema();

  public String getDriver();

  public String getHost();

  public int getPort();

  public String getUser();

  public String getPassword();

  /**
   * Take the url of Parameters.
   *
   * @return url of Parameters
   */
  public String getDatabaseUrl();

  /**
   * Take the Connection Object, after the Connection Object has connected. Than setting it.
   *
   * @param connection db`s connect object
   * @throws SQLException SQL error response from database
   */
  public void setupConnection(Connection connection) throws SQLException;
}
