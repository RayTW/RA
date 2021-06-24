package test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/** Test class. */
public class UnitTestUtils {
  private static ConcurrentHashMap<Integer, ServerSocket> serverSocketPool =
      new ConcurrentHashMap<>();
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  /**
   * Convert bytes to hex string.
   *
   * @param bytes bytes
   * @return hex string
   */
  public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  /** 建立有效port的ServerSocket. */
  public static ServerSocket generateServerSocket() {
    return generateServerSocket(0);
  }

  /**
   * 建立有效port的ServerSocket.
   *
   * @param backlog - requested maximum length of the queue of incoming connections.
   */
  public static ServerSocket generateServerSocket(int backlog) {
    ServerSocket serverSocket = null;
    int port = serverSocketPool.size() == 0 ? 8000 : 8000 + serverSocketPool.size();

    while (port < 60000) {
      if (serverSocketPool.containsKey(port)) {
        port++;
        continue;
      }
      try {
        serverSocket = new ServerSocket(port, backlog);
        serverSocketPool.put(port, serverSocket);
        break;
      } catch (Exception e) {
        e.printStackTrace();
        port++;
      }
    }

    return serverSocket;
  }

  /**
   * Create template file.
   *
   * @param filePath path
   * @param consumer properties
   * @return path
   */
  public static Path createTempPropertiesFile(String filePath, Consumer<Properties> consumer) {
    Path path = Paths.get(filePath);

    try (OutputStream output = new FileOutputStream(path.toString())) {
      Properties properties = new Properties();

      consumer.accept(properties);

      properties.store(output, null);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return path;
  }
}
