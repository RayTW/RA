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

  /** Initialize. */
  public MockDatabaseConnections() {}

  /**
   * Initialize.
   *
   * @param name name
   */
  public MockDatabaseConnections(String name) {
    super(name);
  }

  /**
   * Set listener.
   *
   * @param mock mock statement executor
   */
  public void setMockStatementExecutor(MockStatementExecutor mock) {
    mockStatementExecutor = mock;
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
  public void connect(
      DatabaseParameters param, int count, Callable<DatabaseConnection> connectionMode)
      throws Exception {}

  @Override
  public StatementExecutor getStatementExecutor(int index) {
    return mockStatementExecutor;
  }

  @Override
  public DatabaseConnection getConnection(int index) {
    return null;
  }

  @Override
  public void close() {}
}
