package ra.net.nio;

import java.io.IOException;
import java.net.SocketException;

/**
 * Write byte.
 *
 * @author Ray Li
 */
public interface Writable {
  /**
   * Write.
   *
   * @param bytes bytes
   * @param offset offset
   * @param length length
   * @throws IOException IOException
   * @throws SocketException SocketException
   */
  public void write(byte[] bytes, int offset, int length) throws IOException, SocketException;
}
