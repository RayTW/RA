package ra.db.connection;

import java.sql.Connection;
import ra.db.MockConnection;
import ra.db.parameter.DatabaseParameters;
import ra.db.parameter.MysqlParameters;

/**
 * Mock original connection.
 *
 * @author Ray Li
 */
public class MockOriginalConnection extends OriginalConnection {
  private MockConnection mockConnection = new MockConnection();
  private boolean isKeepLive = true;

  public MockOriginalConnection(MysqlParameters param) {
    super(param);
  }

  @Override
  public void loadDriveInstance(DatabaseParameters param) {}

  public void setIsLive(boolean isLive) {
    isKeepLive = isLive;
  }

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
