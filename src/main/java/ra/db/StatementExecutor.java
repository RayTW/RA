package ra.db;

import java.util.List;
import ra.db.record.LastInsertId;
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
   * Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE statement or an SQL
   * statement that returns nothing, such as an SQL DDL statement.
   *
   * @param sql SQL statement
   * @return affected rows
   */
  public int executeUpdate(String sql) throws RaConnectException, RaSqlException;

  /**
   * Attempts to execute the given SQL statement, which may be an INSERT, UPDATE, or DELETE
   * statement or an SQL statement that returns nothing, such as an SQL DDL statement.
   *
   * @param sql SQL statement
   * @return affected rows
   */
  public int tryExecuteUpdate(String sql) throws RaConnectException, RaSqlException;

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
  public LastInsertId insert(String sql) throws RaConnectException, RaSqlException;

  /**
   * Executes the given SQL statement, which returns a single RecordCursor object.
   *
   * @param sql SQL statement
   * @return RecordCursor
   */
  public RecordCursor executeQuery(String sql) throws RaConnectException, RaSqlException;

  /**
   * Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE statement or an SQL
   * statement that returns nothing, such as an SQL DDL statement.
   *
   * @param prepared prepared
   * @return RecordCursor
   * @throws RaConnectException RaConnectException
   * @throws RaSqlException RaSqlException
   */
  public int prepareExecuteUpdate(Prepared prepared) throws RaConnectException, RaSqlException;

  /**
   * A SQL statement is precompiled and stored in a Prepared object. This object can then be used to
   * efficiently execute this statement multiple times.
   *
   * @param prepared prepared
   * @return RecordCursor
   * @throws RaConnectException RaConnectException
   * @throws RaSqlException RaSqlException
   */
  public RecordCursor prepareExecuteQuery(Prepared prepared)
      throws RaConnectException, RaSqlException;
}
