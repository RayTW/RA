package ra.net;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The queue of message.
 *
 * @author Ray Li
 */
public class SendProcessorKeep extends Thread implements Sendable<String> {
  private NetSocketWriterKeep netSocketPrintKeep;
  private boolean isRunning = true;
  private BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
  private boolean isClearQueue = true;

  /**
   * Initialize.
   *
   * @param net net
   */
  public SendProcessorKeep(NetSocketWriterKeep net) {
    netSocketPrintKeep = net;
  }

  /** Clear message. */
  public void clearQueue() {
    try {
      queue.clear();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Whether to allow the check message to be cleared after disconnection.
   *
   * @param enable default true
   */
  public void enableClearQueue(boolean enable) {
    isClearQueue = enable;
  }

  @Override
  public void run() {
    while (isRunning) {

      while (queue.size() > 0) {
        try {
          netSocketPrintKeep.write(queue.take());
        } catch (Exception e) {
          e.printStackTrace();
          if (isClearQueue) {
            clearQueue();
          }
          netSocketPrintKeep.closeSocket();

          if (!isClearQueue) {
            break;
          }
        }
      }
    }
  }

  /**
   * Send message.
   *
   * @param message message
   */
  @Override
  public void send(String message) {
    queue.add(message);
  }

  /** Close sender. */
  public void close() {
    isRunning = false;
  }

  @Override
  public void sendClose(String message) {
    throw new UnsupportedOperationException("The keep-alive sender can not disconnect.");
  }
}
