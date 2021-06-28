package ra.util.compression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.xerial.snappy.SnappyInputStream;

/**
 * Use snappy to read text files.
 *
 * @author Ray Li
 */
public class SnappyFileStringInput implements StringInput {
  private FileInputStream fileInputStream;
  private BufferedReader bufferedInput;
  private SnappyInputStream compressionInputStream;

  /**
   * Initialize.
   *
   * @param file source format
   * @param charset file charset
   */
  public SnappyFileStringInput(File file, String charset) {
    try {
      fileInputStream = new FileInputStream(file);
      compressionInputStream = new SnappyInputStream(fileInputStream);
      bufferedInput = new BufferedReader(new InputStreamReader(compressionInputStream, charset));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String readLine() throws IOException {
    return bufferedInput.readLine();
  }

  @Override
  public int read() throws IOException {
    return bufferedInput.read();
  }

  @Override
  public void close() throws IOException {
    bufferedInput.close();
  }
}
