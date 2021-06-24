package ra.net.nio;

import java.io.IOException;
import java.net.SocketException;

/**
 * Write byte.
 *
 * @author Ray Li
 */
public interface Writable {
  public void write(byte[] bytes, int offset, int length) throws IOException, SocketException;
}
