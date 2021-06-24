package ra.net.nio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * CySocket base on bytes.
 *
 * @author Ray Li
 */
public class DataSocket {
  private Consumer<Data> onReadListener;
  private Thread thread;
  private Socket socket;
  private BufferedOutputStream bufferedOutputStream;
  private PackageHandleInput input;
  private PackageHandleOutput output;

  /** Initialize. */
  public DataSocket() {
    input = new PackageHandleInput();
    output = new PackageHandleOutput();
  }

  /**
   * 連線指定ip、port.
   *
   * @param ip 位址
   * @param port 埠號
   */
  public boolean connect(String ip, int port) {
    return connect(ip, port, e -> e.printStackTrace());
  }

  /**
   * 連線指定ip、port.
   *
   * @param ip 位址
   * @param port 埠號
   * @param connectTimeout 連線逾時秒數(millisecond)
   * @param soTimeout 讀取逾時秒數(millisecond)
   */
  public boolean connect(String ip, int port, int connectTimeout, int soTimeout) {
    return connect(ip, port, connectTimeout, soTimeout, e -> e.printStackTrace());
  }

  /**
   * 連線指定ip、port.
   *
   * @param ip 位址
   * @param port 埠號
   * @param listener 用來取回IOException
   */
  public boolean connect(String ip, int port, Consumer<IOException> listener) {
    return connect(ip, port, 0, 0, listener);
  }

  /**
   * 連線指定ip、port.
   *
   * @param ip 位址
   * @param port 埠號
   * @param connectTimeout 連線逾時秒數(millisecond)
   * @param soTimeout 讀取逾時秒數(millisecond)
   * @param listener 用來取回IOException
   */
  public boolean connect(
      String ip, int port, int connectTimeout, int soTimeout, Consumer<IOException> listener) {
    try {
      socket = new Socket();
      socket.connect(new InetSocketAddress(ip, port), connectTimeout);
      socket.setSoTimeout(soTimeout);
      bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
      thread =
          new Thread(
              () -> {
                try {
                  doReadByte();
                } catch (IOException e) {
                  if (listener != null) {
                    listener.accept(e);
                  }
                }
              });

      thread.start();
    } catch (IOException e) {
      if (listener != null) {
        listener.accept(e);
      }
      return false;
    }
    return true;
  }

  private void doReadByte() throws IOException {
    try {
      BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

      input.readByte(
          in,
          (dataType, bytes) -> {
            if (onReadListener != null) {
              onReadListener.accept(new Data(dataType, bytes));
            }
            return Boolean.FALSE;
          });
    } finally {
      if (!socket.isClosed()) {
        socket.close();
      }
    }
  }

  public void write(JSONObject json) {
    write(json.toString());
  }

  public void write(JSONArray json) {
    write(json.toString());
  }

  /**
   * Write text.
   *
   * @param text message
   */
  public void write(String text) {
    try {
      output.transfer(
          new Data(DataType.TEXT, text.getBytes()),
          (byte[] bytes, int offset, int length) -> {
            bufferedOutputStream.write(bytes, offset, length);
            bufferedOutputStream.flush();
          });
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  /**
   * Write file.
   *
   * @param path file path
   */
  public void writeFile(Path path) {
    try {
      output.transfer(
          new Data(DataType.ZIP, Files.readAllBytes(path)),
          (byte[] b, int offset, int length) -> {
            bufferedOutputStream.write(b, offset, length);
            bufferedOutputStream.flush();
          });
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  public void close() throws IOException {
    socket.close();
  }

  /**
   * 是否已關閉.
   *
   * @return 是否已關閉
   */
  public boolean isClose() {
    if (socket != null) {
      return socket.isClosed();
    }
    return true;
  }

  /**
   * 連線狀態.
   *
   * @return 連線狀態
   */
  public boolean isConnected() {
    if (socket != null) {
      return socket.isConnected();
    }
    return false;
  }

  public void setOnReadLineListener(Consumer<Data> listener) {
    onReadListener = listener;
  }
}
