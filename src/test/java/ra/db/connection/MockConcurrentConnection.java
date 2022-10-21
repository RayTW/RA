package ra.db.connection;

import java.sql.Connection;
import ra.db.MockConnection;
import ra.db.parameter.DatabaseParameters;

/**
 * Mock class.
 *
 * @author Ray Li
 */
public class MockConcurrentConnection extends ConcurrentConnection {
  private MockConnection mockConnection = new MockConnection();
  private boolean isKeepLive = true;

  /**
   * Initialize.
   *
   * @param param database settings.
   */
  public MockConcurrentConnection(DatabaseParameters param) {
    super(param);
  }

  @Override
  public void loadDriveInstance(DatabaseParameters param) {}

  /**
   * Set state of database connection.
   *
   * @param isLive database available
   */
  public void setIsLive(boolean isLive) {
    isKeepLive = isLive;
  }

  /**
   * Returns mock connection.
   *
   * @return MockConnection
   */
  public MockConnection getMockConnection() {
    return mockConnection;
  }

  @Override
  public Connection getConnection() {
    return mockConnection;
  }

  @Override
  public boolean isLive() {
    return isKeepLive;
  }

  @Override
  public boolean connect() {
    return true;
  }
}
