package ra.db;

import ra.db.JdbcExecutor.Transaction;

/**
 * Transaction Executor.
 *
 * @author ray_lee
 */
public interface TransactionExecutor extends DatabaseOperable<Transaction, Boolean> {}
