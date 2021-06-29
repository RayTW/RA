package ra.net.nio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * DataSocket base on bytes.
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
   * Connect to remote server.
   *
   * @param ip IP address
   * @param port port
   * @return If connect successful returns true.
   */
  public boolean connect(String ip, int port) {
    return connect(ip, port, e -> e.printStackTrace());
  }

  /**
   * Connect to remote server.
   *
   * @param ip IP address
   * @param port port
   * @param connectTimeout connect timeout (millisecond)
   * @param soTimeout soTimeout(millisecond)
   * @return If connect successful returns true.
   */
  public boolean connect(String ip, int port, int connectTimeout, int soTimeout) {
    return connect(ip, port, connectTimeout, soTimeout, e -> e.printStackTrace());
  }

  /**
   * Connect to remote server.
   *
   * @param ip IP address
   * @param port port
   * @param listener IOException
   * @return If connect successful returns true.
   */
  public boolean connect(String ip, int port, Consumer<IOException> listener) {
    return connect(ip, port, 0, 0, listener);
  }

  /**
   * Connect to remote server.
   *
   * @param ip IP address
   * @param port port
   * @param connectTimeout connect timeout(millisecond)
   * @param soTimeout soTimeout(millisecond)
   * @param listener IOException
   * @return If connect successful returns true.
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
          (data) -> {
            if (onReadListener != null) {
              onReadListener.accept(data);
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
          new Data(text),
          (byte[] bytes, int offset, int length) -> {
            bufferedOutputStream.write(bytes, offset, length);
            bufferedOutputStream.flush();
          });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Write file.
   *
   * @param path file path
   */
  public void write(Path path) {
    try {

      output.transfer(
          new Data(path),
          (byte[] b, int offset, int length) -> {
            bufferedOutputStream.write(b, offset, length);
            bufferedOutputStream.flush();
          });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void write(File file) {
    write(Paths.get(file.toString()));
  }

  public void close() throws IOException {
    socket.close();
  }

  /**
   * Whether connection has closed.
   *
   * @return If connection has closed returns true.
   */
  public boolean isClose() {
    if (socket != null) {
      return socket.isClosed();
    }
    return true;
  }

  /**
   * Whether connection has connected.
   *
   * @return If connection has connected returns true.
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
