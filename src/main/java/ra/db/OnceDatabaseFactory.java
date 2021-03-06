package ra.db;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import ra.db.connection.OnceConnection;
import ra.db.parameter.DatabaseParameters;

/**
 * Get connection use asynchronous mode.
 *
 * @author Ray Li
 */
public class OnceDatabaseFactory {
  private Supplier<DatabaseParameters> databaseParam;
  private AtomicLong databaseCount;

  /**
   * Initialize.
   *
   * @param param database settings.
   */
  public OnceDatabaseFactory(Supplier<DatabaseParameters> param) {
    databaseParam = param;
    databaseCount = new AtomicLong(0);
  }

  /**
   * Initialize.
   *
   * @param param database settings.
   */
  public OnceDatabaseFactory(DatabaseParameters param) {
    this(() -> param);
  }

  /**
   * Get once database connection, It will be close after use.
   *
   * @param comsumer Get {@link StatementExecutor}
   */
  public void getAndClose(Consumer<StatementExecutor> comsumer) {
    try (DatabaseConnection mydb = new OnceConnection(databaseParam.get())) {
      databaseCount.incrementAndGet();

      mydb.connectIf(comsumer::accept);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      databaseCount.decrementAndGet();
    }
  }

  /**
   * Get current connection count.
   *
   * @return connection count
   */
  public long getCount() {
    return databaseCount.get();
  }
}
