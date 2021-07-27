package ra.net;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Receive messages and put to queue.
 *
 * @author Ray Li
 */
public class MessageReceiver extends Thread {
  private boolean isRunning = true;
  private List<byte[]> queue = Collections.synchronizedList(new LinkedList<byte[]>());
  private Consumer<String> onReceiveMessageListener;
  private ReentrantLock lock = new ReentrantLock();
  private Condition condition = lock.newCondition();

  /**
   * Initialize.
   *
   * @param listener listener
   */
  public MessageReceiver(Consumer<String> listener) {
    onReceiveMessageListener = listener;
  }

  /** Clear queue all messages. */
  public void clearQueue() {
    try {
      queue.clear();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Close thread. */
  public void close() {
    try {
      queue.clear();
      isRunning = false;
    } catch (Exception e) {
      e.printStackTrace();
    }
    awake();
  }

  @Override
  public void run() {
    while (isRunning) {
      byte[] msg;

      while (queue.size() > 0) {
        try {
          msg = queue.remove(0);
          onReceiveMessageListener.accept(new String(msg));
        } catch (Exception e) {
          e.printStackTrace();
        }
        msg = TransmissionEnd.BYTES_ZERO;
      }
      lock.lock();
      try {
        try {
          condition.await(50, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } finally {
        lock.unlock();
      }
    }
  }

  /**
   * Put the message to queue.
   *
   * @param msg message
   */
  public void put(String msg) {
    queue.add(msg.getBytes());
  }

  /**
   * Put the message to the queue and awake queue.
   *
   * @param msg message
   */
  public void putAndAwake(String msg) {
    put(msg);
    awake();
  }

  /** Awake thread. */
  public void awake() {
    lock.lock();
    try {
      condition.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Returns running state.
   *
   * @return running state
   */
  public boolean isRun() {
    return isRunning;
  }
}
