package ra.db;

/** Verify the database connection is alive. */
public class DatabaseKeepAlive extends Thread {
  private boolean isRunning = true;

  private KeepAlive keepalive;

  public DatabaseKeepAlive(KeepAlive listener) {
    this.keepalive = listener;
  }

  @Override
  public void run() {

    while (isRunning) {
      try {
        Thread.sleep(keepalive.interval());
      } catch (InterruptedException e) {
        this.interrupt();
      }

      keepalive.keep();
    }

    keepalive = null;
  }

  public void close() {
    isRunning = false;
    this.interrupt();
  }
}
