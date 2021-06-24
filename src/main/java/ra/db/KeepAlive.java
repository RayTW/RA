package ra.db;

/**
 * Verify availability.
 *
 * @author Ray Li
 */
public interface KeepAlive {

  /**
   * Keep-alive interval time.
   *
   * @return time
   */
  public long interval();

  /** Trigger keep-alive event. */
  public void keep();
}
