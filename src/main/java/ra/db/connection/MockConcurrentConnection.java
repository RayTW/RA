package ra.db.connection;

import java.sql.Connection;
import ra.db.MockConnection;
import ra.db.parameter.DatabaseParameters;
import ra.db.parameter.MysqlParameters;

/**
 * .
 *
 * @author Ray Li
 */
public class MockConcurrentConnection extends ConcurrentConnection {
  private MockConnection mockConnection = new MockConnection();
  private boolean isKeepLive = true;

  public MockConcurrentConnection(MysqlParameters param) {
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
