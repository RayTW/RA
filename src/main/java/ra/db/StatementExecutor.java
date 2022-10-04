package ra.db;

import java.util.List;
import ra.db.record.RecordCursor;
import ra.exception.RaConnectException;
import ra.exception.RaSqlException;

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
  public int execute(String sql) throws RaConnectException, RaSqlException;

  /**
   * Try to execute SQL statement (CRUD).
   *
   * @param sql SQL statement
   * @return affected rows
   */
  public int tryExecute(String sql) throws RaConnectException, RaSqlException;

  /**
   * Execute SQL statements.
   *
   * @param executor executor
   * @throws RaConnectException RaConnectException
   * @throws RaSqlException RaSqlException
   */
  public void executeTransaction(TransactionExecutor executor)
      throws RaConnectException, RaSqlException;

  /**
   * Execute SQL statements.
   *
   * @param sql SQL statement
   * @return affected rows
   */
  public int executeCommit(List<String> sql) throws RaConnectException, RaSqlException;

  /**
   * Return the last id after executing SQL statement.
   *
   * @param sql SQL statement
   * @return last id
   */
  public int insert(String sql) throws RaConnectException, RaSqlException;

  /**
   * Execute query, ex : SELECT * FROM table.
   *
   * @param sql SQL statement
   * @return RecordCursor
   */
  public RecordCursor executeQuery(String sql) throws RaConnectException, RaSqlException;

  /**
   * A SQL statement is precompiled and stored in a Prepared object. This object can then be used to
   * efficiently execute this statement multiple times.
   *
   * @param prepared prepared
   * @return RecordCursor
   * @throws RaConnectException RaConnectException
   * @throws RaSqlException RaSqlException
   */
  public RecordCursor executeQueryUsePrepare(Prepared prepared)
      throws RaConnectException, RaSqlException;
}
