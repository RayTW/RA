package ra.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Optional;
import java.util.function.Function;

/**
 * Mock SQL statement.
 *
 * @author Ray Li
 */
public class MockStatement implements Statement {
  private Optional<Function<String, Integer>> executeUpdateListener;
  private Optional<Function<String, ResultSet>> executeQueryListener;
  private Optional<Function<String, Boolean>> executeListener;
  private SQLException throwException;
  private ResultSet generatedKeys;

  /** Initialize. */
  public MockStatement() {
    executeUpdateListener = Optional.ofNullable(null);
    executeQueryListener = Optional.ofNullable(null);
    executeListener = Optional.ofNullable(null);
  }

  public <T extends SQLException> void setThrowExceptionAnyExecute(T exception) {
    throwException = exception;
  }

  protected void throwException() throws SQLException {
    if (throwException == null) {
      return;
    }

    throw throwException;
  }

  /**
   * The column name of the last inserter ID at query time.
   *
   * @param result result
   */
  public void setGeneratedKeys(ResultSet result) {
    generatedKeys = result;
  }

  /**
   * Register listener on event that execute the update.
   *
   * @param listener listener
   */
  public void setExecuteUpdateListener(Function<String, Integer> listener) {
    executeUpdateListener = Optional.ofNullable(listener);
  }

  /**
   * Register listener on event that execute the query.
   *
   * @param listener listener
   */
  public void setExecuteQueryListener(Function<String, ResultSet> listener) {
    executeQueryListener = Optional.ofNullable(listener);
  }

  /**
   * Register listener on event that execute operate.
   *
   * @param listener listener
   */
  public void setExecuteListener(Function<String, Boolean> listener) {
    executeListener = Optional.ofNullable(listener);
  }

  @Override
  public void addBatch(String sql) throws SQLException {}

  @Override
  public void cancel() throws SQLException {}

  @Override
  public void clearBatch() throws SQLException {}

  @Override
  public void clearWarnings() throws SQLException {}

  @Override
  public void close() throws SQLException {}

  @Override
  public void closeOnCompletion() throws SQLException {}

  @Override
  public boolean execute(String sql) throws SQLException {
    throwException();
    return executeListener.orElse(o -> true).apply(sql);
  }

  @Override
  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
    throwException();
    return executeListener.orElse(o -> true).apply(sql);
  }

  @Override
  public boolean execute(String sql, int[] columnIndexes) throws SQLException {
    throwException();
    return executeListener.orElse(o -> true).apply(sql);
  }

  @Override
  public boolean execute(String sql, String[] columnNames) throws SQLException {
    throwException();
    return executeListener.orElse(o -> true).apply(sql);
  }

  @Override
  public int[] executeBatch() throws SQLException {
    throwException();
    return null;
  }

  @Override
  public ResultSet executeQuery(String sql) throws SQLException {
    throwException();
    return executeQueryListener.orElse(o -> new MockResultSet(new String[0])).apply(sql);
  }

  @Override
  public int executeUpdate(String sql) throws SQLException {
    throwException();
    return executeUpdateListener.orElse(o -> 0).apply(sql);
  }

  @Override
  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    throwException();
    return executeUpdateListener.orElse(o -> 0).apply(sql);
  }

  @Override
  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
    throwException();
    return executeUpdateListener.orElse(o -> 0).apply(sql);
  }

  @Override
  public int executeUpdate(String sql, String[] columnNames) throws SQLException {
    throwException();
    return executeUpdateListener.orElse(o -> 0).apply(sql);
  }

  @Override
  public Connection getConnection() throws SQLException {
    return null;
  }

  @Override
  public int getFetchDirection() throws SQLException {
    return 0;
  }

  @Override
  public int getFetchSize() throws SQLException {
    return 0;
  }

  @Override
  public ResultSet getGeneratedKeys() throws SQLException {
    return generatedKeys;
  }

  @Override
  public int getMaxFieldSize() throws SQLException {
    return 0;
  }

  @Override
  public int getMaxRows() throws SQLException {
    return 0;
  }

  @Override
  public boolean getMoreResults() throws SQLException {
    return false;
  }

  @Override
  public boolean getMoreResults(int current) throws SQLException {
    return false;
  }

  @Override
  public int getQueryTimeout() throws SQLException {
    return 0;
  }

  @Override
  public ResultSet getResultSet() throws SQLException {
    return null;
  }

  @Override
  public int getResultSetConcurrency() throws SQLException {
    return 0;
  }

  @Override
  public int getResultSetHoldability() throws SQLException {
    return 0;
  }

  @Override
  public int getResultSetType() throws SQLException {
    return 0;
  }

  @Override
  public int getUpdateCount() throws SQLException {
    return 0;
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    return null;
  }

  @Override
  public boolean isCloseOnCompletion() throws SQLException {
    return false;
  }

  @Override
  public boolean isClosed() throws SQLException {
    return false;
  }

  @Override
  public boolean isPoolable() throws SQLException {
    return false;
  }

  @Override
  public void setCursorName(String name) throws SQLException {}

  @Override
  public void setEscapeProcessing(boolean enable) throws SQLException {}

  @Override
  public void setFetchDirection(int direction) throws SQLException {}

  @Override
  public void setFetchSize(int rows) throws SQLException {}

  @Override
  public void setMaxFieldSize(int max) throws SQLException {}

  @Override
  public void setMaxRows(int max) throws SQLException {}

  @Override
  public void setPoolable(boolean poolable) throws SQLException {}

  @Override
  public void setQueryTimeout(int seconds) throws SQLException {}

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return null;
  }
}
