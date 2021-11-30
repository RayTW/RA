package ra.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Provide sent message from the queue.
 *
 * @author Ray Li, Kevin Tasi
 */
public class SendProcessor implements Runnable, Sendable<String> {
  private NetServiceable netServiceable;
  private boolean isRunning = true;
  private BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
  private Socket socket;
  private BufferedOutputStream bufferedOutputStream;
  private int timeOut = 0;
  private boolean sendComplete = false;

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

  /**
   * Initialize.
   *
   * @param service service
   */
  public SendProcessor(NetServiceable service) {
    netServiceable = service;
  }

  /** Clear message in queue. */
  public void clearQueue() {
    try {
      queue.clear();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Close and clear message in queue. */
  public void close() {
    isRunning = false;
  }

  @Override
  public void run() {
    while (isRunning) {
      try {
        flushMessage(queue.take());
      } catch (Exception e) {
        close();
        netServiceable.onClose();
        e.printStackTrace();
        break;
      }
      try {
        if (sendComplete) {
          close();
          netServiceable.onClose();
        }
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
  }

  /**
   * Close the connection after sent the message.
   *
   * @param message message
   */
  @Override
  public void sendClose(String message) {
    send(message);
    setSendComplete(true);
  }

  private void flushMessage(String msg) throws Exception {
    socket.setSoTimeout(10000);

    bufferedOutputStream.write(TransmissionEnd.appendFeedNewLine(msg).getBytes());
    bufferedOutputStream.flush();
    socket.setSoTimeout(timeOut);
  }

  private void flushQueue() {
    String message;
    while (queue.size() > 0) {
      try {
        message = queue.poll();
        flushMessage(message);
      } catch (Exception e) {
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

  /**
   * Return whether completed that sent the message.
   *
   * @return whether sent completed
   */
  public boolean getSendCompilete() {
    return sendComplete;
  }

  private void setSendComplete(boolean complete) {
    sendComplete = complete;
  }
}
