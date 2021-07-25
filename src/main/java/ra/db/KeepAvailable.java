package ra.db;

/**
 * Keep the database connection permanently connected.
 *
 * @author Ray Li
 */
public interface KeepAvailable extends DatabaseConnection, KeepAlive {

  @Override
  public default long interval() {
    return 5000;
  }
}
