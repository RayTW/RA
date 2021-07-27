package ra.net;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The queue of message.
 *
 * @author Ray Li
 */
public class SendProcessorKeep extends Thread implements Sendable<String> {
  private NetSocketWriterKeep netSocketPrintKeep;
  private boolean isRunning = true;
  private List<String> queue = Collections.synchronizedList(new LinkedList<String>());
  private ReentrantLock lock = new ReentrantLock();
  private Condition condition = lock.newCondition();
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
      String message;

      while (queue.size() > 0) {
        try {
          message = queue.get(0);

          netSocketPrintKeep.write(message);
          queue.remove(0);
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
        message = "";
      }

      lock.lock();
      try {
        condition.await(50, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        lock.unlock();
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
    lock.lock();
    try {
      condition.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /** Close sender. */
  public void close() {
    isRunning = false;
    lock.lock();
    try {
      condition.signalAll();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void sendClose(String message) {
    throw new UnsupportedOperationException("The keep-alive sender can not disconnect.");
  }
}
