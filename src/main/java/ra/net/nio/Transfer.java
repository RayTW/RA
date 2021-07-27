package ra.net.nio;

import java.io.IOException;
import java.net.SocketException;

/**
 * Transfer layer.
 *
 * @author Ray Li
 */
public interface Transfer {
  /**
   * Transfer.
   *
   * @param data source data
   * @param lisener target
   * @throws SocketException SocketException
   * @throws IOException IOException
   */
  public void transfer(Data data, Writable lisener) throws SocketException, IOException;
}
