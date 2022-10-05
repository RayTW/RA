package ra.db;

import ra.db.JdbcExecutor.Transaction;

/**
 * Transaction Executor.
 *
 * @author Ray Li
 */
public interface TransactionExecutor extends DatabaseOperable<Transaction, Boolean> {}
