package ra.util.compression;

import java.io.IOException;

/**
 * 輸出字串.
 *
 * @author Ray Li
 */
public interface StringOutput extends AutoCloseable {
  public void write(String str) throws IOException;

  public void flush() throws IOException;
}
