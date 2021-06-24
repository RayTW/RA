package ra.db;

import java.util.concurrent.Callable;
import ra.db.parameter.DatabaseParameters;

/**
 * Mock database connections.
 *
 * @author Ray Li
 */
public class MockDatabaseConnections extends DatabaseConnections {
  private MockStatementExecutor mockStatementExecutor = new MockStatementExecutor();

  public MockDatabaseConnections() {}

  public MockDatabaseConnections(String name) {
    super(name);
  }

  public void setMockRecordSet2(MockStatementExecutor mock) {
    mockStatementExecutor = mock;
  }

  public MockStatementExecutor getMockRecordSet2() {
    return mockStatementExecutor;
  }

  @Override
  public void connectOriginalConnection(DatabaseParameters param, int count) {}

  @Override
  public StatementExecutor next() {
    return mockStatementExecutor;
  }

  @Override
  public String getName() {
    return super.getName();
  }

  @Override
  public void connect(DatabaseParameters param, int count, Callable<DatabaseConnection> newDbObj)
      throws Exception {}

  @Override
  public StatementExecutor getStatementExecutor(int index) {
    return mockStatementExecutor;
  }

  @Override
  public DatabaseConnection getDbConnection(int index) {
    return null;
  }

  @Override
  public void close() {}
}
