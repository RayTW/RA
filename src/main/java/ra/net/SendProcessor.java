package ra.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Provide sent message from the queue.
 *
 * @author Ray Li, Kevin Tasi
 */
public class SendProcessor extends Thread implements Sendable<String> {
  private NetServiceable netServiceable;
  private boolean isRunning = true;
  private List<String> queue = Collections.synchronizedList(new ArrayList<String>());
  private Socket socket;
  private BufferedOutputStream bufferedOutputStream;
  private int timeOut = 0;
  private boolean sendCompilete = false;

  /**
   * Initialize.
   *
   * @param service service
   * @param socket socket
   * @param timeout timeout
   * @throws IOException IOException
   */
  public SendProcessor(NetServiceable service, Socket socket, int timeout) throws IOException {
    netServiceable = service;
    this.socket = socket;
    this.timeOut = timeout;
    bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
  }

  public SendProcessor(NetServiceable net) {
    netServiceable = net;
  }

  /** Clear message in queue. */
  public void clearQue() {
    try {
      queue.clear();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Close and clear message in queue. */
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
      String msg;
      while (queue.size() > 0) {
        try {
          msg = queue.get(0);
          flushMessage(msg);
          queue.remove(0);

        } catch (Exception e) {
          close();
          netServiceable.onClose();
          e.printStackTrace();
          break;
        }
        msg = "";
      }
      try {
        if (sendCompilete) {
          close();
          netServiceable.onClose();
        }

        synchronized (this) {
          wait(50);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    flushQue();
    clearQue();

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

    netServiceable = null;
  }

  /**
   * Send message.
   *
   * @param message message
   */
  @Override
  public void send(String message) {
    Objects.requireNonNull(message, "The message requires a non null, message = " + message);
    queue.add(message);
    synchronized (this) {
      notifyAll();
    }
  }

  /**
   * Close the connection after sent the message.
   *
   * @param message message
   */
  @Override
  public void sendClose(String message) {
    send(message);
    setSendCompilete(true);
  }

  private void flushMessage(String msg) throws Exception {
    socket.setSoTimeout(10000);

    bufferedOutputStream.write(TransmissionEnd.appendFeedNewLine(msg).getBytes());
    bufferedOutputStream.flush();
    socket.setSoTimeout(timeOut);
  }

  private void flushQue() {
    String message;
    while (queue.size() > 0) {
      try {
        message = queue.get(0);
        flushMessage(message);
        queue.remove(0);
      } catch (Exception e) { // 若送出訊息發生IOException則不繼續執行清queue
        e.printStackTrace();
        break;
      }
      message = "";
    }
  }

  /**
   * Returns IP address.
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
   * Set timeout value.
   *
   * @param timeout timeout
   * @throws SocketException SocketException
   */
  public void setSoTimeout(int timeout) throws SocketException {
    if (socket != null) {
      socket.setSoTimeout(timeout);
    }
  }

  public boolean getSendCompilete() {
    return sendCompilete;
  }

  private void setSendCompilete(boolean compilete) {
    sendCompilete = compilete;
  }
}
