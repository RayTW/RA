package ra.net;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Provider socket write and read.
 *
 * @author Ray Li
 */
public class NetSocketWriter {
  private String host;
  private int port;
  private int sendTimeout = 10 * 1000; // second

  private NetSocketWriter() {}

  /**
   * Sent message synchronous.
   *
   * @param message message
   * @param connectTimeout connection timeout
   * @return Returns receive message
   * @throws IOException IOException
   * @throws UnknownHostException UnknownHostException
   */
  public String send(String message, int connectTimeout) throws UnknownHostException, IOException {
    String ret = "";
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(host, port), connectTimeout);
      BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
      out.write(TransmissionEnd.appendFeedNewLine(message).getBytes());
      out.flush();
      BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      socket.setSoTimeout(sendTimeout);
      ret = br.readLine();
    }
    return ret;
  }

  /**
   * Sent message synchronous.
   *
   * @param message 發送的訊息
   * @return Returns receive message.
   * @throws IOException IOException
   * @throws UnknownHostException UnknownHostException
   */
  public String send(String message) throws UnknownHostException, IOException {
    String ret = "";
    try (Socket socket = new Socket(host, port)) {

      BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
      out.write(TransmissionEnd.appendFeedNewLine(message).getBytes());
      out.flush();
      BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      socket.setSoTimeout(sendTimeout);
      ret = br.readLine();
    }
    return ret.length() > 0 ? ret.substring(0, ret.length() - 1) : ret;
  }

  /**
   * Sent message asynchronous.
   *
   * @param message message
   * @throws IOException Sent message asynchronous.
   * @throws UnknownHostException UnknownHostException
   */
  public void sendAsync(String message) throws UnknownHostException, IOException {
    try (Socket socket = new Socket(host, port)) {
      socket.setSoTimeout(sendTimeout);
      BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
      out.write(TransmissionEnd.appendFeedNewLine(message).getBytes());
      out.flush();
    }
  }

  /** NetSocketPrint. */
  public static class Builder {
    private String host;
    private int port;
    private Integer sendTimeout = null; // second

    /**
     * Initialize.
     *
     * @return {@link NetSocketWriter}
     */
    public NetSocketWriter build() {
      NetSocketWriter obj = new NetSocketWriter();

      obj.host = host;
      obj.port = port;

      if (sendTimeout != null) {
        obj.sendTimeout = sendTimeout;
      }
      return obj;
    }

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public Builder setPort(int port) {
      this.port = port;
      return this;
    }

    public Builder setSendTimeOut(int timeout) {
      this.sendTimeout = timeout;
      return this;
    }
  }
}
