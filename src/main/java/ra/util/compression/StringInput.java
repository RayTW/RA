package ra.util.compression;

import java.io.IOException;

/**
 * Read string.
 *
 * @author Ray Li
 */
public interface StringInput extends AutoCloseable {
  /**
   * Reads a single string.
   *
   * @return string
   * @throws IOException IOException
   */
  public String readLine() throws IOException;

  /**
   * Reads a single character.
   *
   * @return The character read, as an integer in the range 0 to 65535 (0x00-0xffff), or -1 if the
   *     end of the stream has been reached
   * @throws IOException If an I/O error occurs
   */
  public int read() throws IOException;
}
