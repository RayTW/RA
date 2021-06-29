package ra.util.compression;

import java.io.IOException;

/**
 * Write string.
 *
 * @author Ray Li
 */
public interface StringOutput extends AutoCloseable {
  public void write(String str) throws IOException;

  public void flush() throws IOException;
}
