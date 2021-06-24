package ra.net.nio;

import java.io.IOException;
import java.net.SocketException;

/**
 * .
 *
 * @author Ray Li
 */
public interface Transfer {
  public void transfer(Data data, Writable lisener) throws SocketException, IOException;
}
