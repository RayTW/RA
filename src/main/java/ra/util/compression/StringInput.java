package ra.util.compression;

import java.io.IOException;

/**
 * Read string.
 *
 * @author Ray Li
 */
public interface StringInput extends AutoCloseable {
  public String readLine() throws IOException;

  public int read() throws IOException;
}
