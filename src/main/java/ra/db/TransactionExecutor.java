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
  public boolean apply(Transaction transacation) throws ConnectException, SQLException;
}
