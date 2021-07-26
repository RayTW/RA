package ra.db;

import java.util.List;
import java.util.function.Consumer;
import ra.db.record.RecordCursor;

/**
 * SQL statement (CRUD) executor.
 *
 * @author Ray Li
 */
public interface StatementExecutor {

  /**
   * Returns database state whether available.
   *
   * @return database state
   */
  public boolean isLive();

  /**
   * If the execution is successful, the return count.
   *
   * @param sql SQL statement
   * @return affected rows
   */
  public int execute(String sql);

  /**
   * If the execution is successful, the return count.
   *
   * @param sql SQL statements
   * @param listener exception
   * @return affected rows
   */
  public int execute(String sql, Consumer<Exception> listener);

  /**
   * Try to execute SQL statement (CRUD).
   *
   * @param sql SQL statement
   * @return affected rows
   */
  public int tryExecute(String sql);

  /**
   * Try to execute SQL statement (CRUD).
   *
   * @param sql SQL statement
   * @param listener exception
   * @return affected rows
   */
  public int tryExecute(String sql, Consumer<Exception> listener);

  /**
   * Execute SQL statements.
   *
   * @param sql SQL statement
   * @return affected rows
   */
  public int executeCommit(List<String> sql);

  /**
   * Execute SQL statements.
   *
   * @param sql SQL statement
   * @param listener exception
   * @return affected rows
   */
  public int executeCommit(List<String> sql, Consumer<Exception> listener);

  /**
   * Return the last id after executing SQL statement.
   *
   * @param sql SQL statement
   * @return last id
   */
  public int insert(String sql);

  /**
   * Return the last id after executing SQL statement.
   *
   * @param sql SQL statement
   * @param errorListener exception
   * @return affected rows
   */
  public int insert(String sql, Consumer<Exception> errorListener);

  /**
   * Execute query, ex : SELECT * FROM table.
   *
   * @param sql SQL statement
   * @return RecordCursor
   */
  public RecordCursor executeQuery(String sql);

  /**
   * Execute query, ex : SELECT * FROM table.
   *
   * @param sql SQL statement
   * @param exceptionListener SQLException statement invalid、ConnectException connect to database
   *     failed
   * @return RecordCursor
   */
  public RecordCursor executeQuery(String sql, Consumer<Exception> exceptionListener);

  /**
   * Execute query( transaction), ex : SELECT * FROM table.
   *
   * @param listener listener
   */
  public void multiQuery(Consumer<MultiQuery> listener);

  /**
   * Execute query( transaction), ex : SELECT * FROM table.
   *
   * @param listener listener
   * @param exceptionListener exceptionListener
   */
  public void multiQuery(Consumer<MultiQuery> listener, Consumer<Exception> exceptionListener);
}
