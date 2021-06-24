package ra.util.compression;

import java.io.IOException;

/**
 * 讀取字串.
 *
 * @author Ray Li
 */
public interface StringInput extends AutoCloseable {
  public String readLine() throws IOException;

  public int read() throws IOException;
}
