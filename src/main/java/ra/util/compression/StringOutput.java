package ra.util.compression;

import java.io.IOException;

/**
 * Write string.
 *
 * @author Ray Li
 */
public interface StringOutput extends AutoCloseable {
  /**
   * Writes a string.
   *
   * @param str string
   * @throws IOException If an I/O error occurs
   */
  public void write(String str) throws IOException;

  /**
   * Flushes the stream.
   *
   * @throws IOException If an I/O error occurs
   */
  public void flush() throws IOException;
}
