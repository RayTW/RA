package ra.db.connection;

import java.sql.Connection;
import ra.db.MockConnection;
import ra.db.parameter.DatabaseParameters;

/**
 * Mock original connection.
 *
 * @author Ray Li
 */
public class MockOriginalConnection extends OriginalConnection {
  private MockConnection mockConnection = new MockConnection();
  private boolean isKeepLive = true;

  /**
   * Initialize.
   *
   * @param param database connection settings.
   */
  public MockOriginalConnection(DatabaseParameters param) {
    super(param);
  }

  @Override
  public void loadDriveInstance(DatabaseParameters param) {}

  /**
   * Set database available state.
   *
   * @param isLive database state
   */
  public void setIsLive(boolean isLive) {
    isKeepLive = isLive;
  }

  /**
   * Returns mock database connection.
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
