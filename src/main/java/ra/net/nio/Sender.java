package ra.net.nio;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ra.net.Sendable;
import ra.net.Serviceable;

/**
 * Provide sent message use queue.
 *
 * @author Ray Li
 */
public class Sender<E> implements Runnable, Sendable<Data> {
  private Serviceable<E> netListener;
  private Transfer transferListener;
  private boolean isRunning = true;
  private List<Data> queue = Collections.synchronizedList(new ArrayList<Data>(4096));
  private Socket socket;
  private BufferedOutputStream bufferedOutputStream;
  private int timeOut = 0;
  private boolean sendClose = false;

  /**
   * Initialize.
   *
   * @param listener close event
   * @param transferListener transfer
   * @param socket connection
   * @param timeout connection read timeout
   * @throws IOException Throw IOException when the connection failed.
   */
  public Sender(Serviceable<E> listener, Transfer transferListener, Socket socket, int timeout)
      throws IOException {
    netListener = listener;
    this.transferListener = transferListener;
    this.socket = socket;
    timeOut = timeout;
    bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
  }

  /** Clear message queue. */
  public void clearQueue() {
    try {
      queue.clear();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Close and clear message queue. */
  public void close() {
    isRunning = false;
    try {
      synchronized (this) {
        notifyAll();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    while (isRunning) {
      Data msg;
      while (queue.size() > 0) {
        try {
          msg = queue.get(0);
          flushData(msg);
          queue.remove(0);

        } catch (Exception e) {
          e.printStackTrace();
          close();
          netListener.onClose();
          break;
        }
        msg = null;
      }
      try {
        if (sendClose) {
          close();
          netListener.onClose();
        }

        synchronized (this) {
          wait(50);
        }
      } catch (InterruptedException e) {
        // do not thing.
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    flushQueue();
    clearQueue();
    release();
  }

  private void release() {
    if (socket != null) {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    if (bufferedOutputStream != null) {
      try {
        bufferedOutputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      bufferedOutputStream = null;
    }

    netListener = null;
    transferListener = null;
  }

  /**
   * Send data.
   *
   * @param data text or file
   */
  @Override
  public void send(Data data) {
    queue.add(data);
    synchronized (this) {
      notifyAll();
    }
  }

  /** Close sender after sending data. */
  @Override
  public void sendClose(Data data) {
    send(data);
    sendClose = true;
  }

  private void flushData(Data data) throws Exception {
    // data transfer to bytes
    transferListener.transfer(
        data,
        (pkg, offset, length) -> {
          if (pkg != null) {
            socket.setSoTimeout(10000);

            bufferedOutputStream.write(pkg, offset, length);
            bufferedOutputStream.flush();
            socket.setSoTimeout(timeOut);
          }
        });
  }

  // If the socket is not disconnected, send the message in the queue.
  private void flushQueue() {
    Data msg;
    while (queue.size() > 0) {
      try {
        msg = queue.get(0);
        flushData(msg);
        queue.remove(0);
      } catch (Exception e) {
        e.printStackTrace();
        break;
      }
      msg = null;
    }
  }

  /**
   * Returns IP address of client.
   *
   * @return IP address
   */
  public String getIp() {
    if (socket != null) {
      return socket.getInetAddress().toString().replaceAll("/", "");
    }
    return null;
  }

  /**
   * Enable/disable SO_TIMEOUT with the specified timeout, in milliseconds.
   *
   * @param timeout timeout
   * @throws SocketException SocketException
   */
  public void setSoTimeout(int timeout) throws SocketException {
    if (socket != null) {
      socket.setSoTimeout(timeout);
    }
  }

  /**
   * Returns send close of the flag.
   *
   * @return flag
   */
  public boolean isSendClose() {
    return sendClose;
  }
}
