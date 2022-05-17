package ra.db;

import java.net.ConnectException;
import java.sql.SQLException;
import ra.db.JdbcExecutor.Transaction;

/**
 * Transaction Executor.
 *
 * @author ray_lee
 */
public interface TransactionExecutor {
  /**
   * Returns Instance of transaction.
   *
   * @param transaction transaction
   * @return boolean if true will to roll back operation of commit in database.
   * @throws ConnectException ConnectException
   * @throws SQLException SQLException
   */
  public boolean apply(Transaction transaction) throws ConnectException, SQLException;
}
