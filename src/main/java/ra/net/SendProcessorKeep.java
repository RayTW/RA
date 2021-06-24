package ra.net;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 將要傳送的訊息排queue 每50毫秒檢查1次 不提供停止、關閉的功能.
 *
 * @author Ray Li
 */
public class SendProcessorKeep extends Thread implements Sendable<String> {
  private NetSocketWriterKeep netSocketPrintKeep;
  private boolean runThread = true;
  private List<String> queue = Collections.synchronizedList(new LinkedList<String>());
  private ReentrantLock lock = new ReentrantLock();
  private Condition condition = lock.newCondition();
  private boolean isClearQueue = true;

  public SendProcessorKeep(NetSocketWriterKeep net) {
    netSocketPrintKeep = net;
  }

  /** 清空緩存的訊息. */
  public void clearQueue() {
    try {
      queue.clear();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 是否啟用斷線後清空緩存訊息，預設為true(啟用).
   *
   * @param enable default true
   */
  public void enableClearQueue(boolean enable) {
    isClearQueue = enable;
  }

  @Override
  public void run() {
    while (runThread) {
      String msg;

      while (queue.size() > 0) {
        try {
          msg = queue.get(0);

          netSocketPrintKeep.send(msg);
          queue.remove(0);
        } catch (Exception e) {
          e.printStackTrace();
          if (isClearQueue) {
            clearQueue();
          }
          netSocketPrintKeep.close();

          if (!isClearQueue) {
            break;
          }
        }
        msg = "";
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
    runThread = false;
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
