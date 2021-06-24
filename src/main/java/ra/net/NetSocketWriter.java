package ra.net;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 可指定host、port用socket進行單次連線發送、接收訊息.
 *
 * @author Ray Li
 */
public class NetSocketWriter {
  private String host;
  private int port;
  private int sendTimeout = 10 * 1000; // second

  private NetSocketWriter() {}

  /**
   * 送出資料等待回應.
   *
   * @param message 發送的訊息
   * @param connectTimeout 連線的timeout時間
   * @return 回應的訊息
   * @throws IOException io相關的error
   * @throws UnknownHostException 無法識別host時拋出
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
   * 送出資料等待回應.
   *
   * @param message 發送的訊息
   * @return 回應的訊息
   * @throws IOException io相關的error
   * @throws UnknownHostException 無法識別host時拋出
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
   * 送出不等回應.
   *
   * @param message 發送的訊息
   * @throws IOException io相關的error
   * @throws UnknownHostException 無法識別host時拋出
   */
  public void sendAsync(String message) throws UnknownHostException, IOException {
    try (Socket socket = new Socket(host, port)) {
      socket.setSoTimeout(sendTimeout);
      BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
      out.write(TransmissionEnd.appendFeedNewLine(message).getBytes());
      out.flush();
    }
  }

  /** 建構NetSocketPrint. */
  public static class Builder {
    private String host;
    private int port;
    private Integer sendTimeout = null; // second

    /** Initialize. */
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
