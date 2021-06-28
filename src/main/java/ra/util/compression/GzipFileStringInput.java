package ra.util.compression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

/**
 * Use gzip to read text files.
 *
 * @author Ray Li
 */
public class GzipFileStringInput implements StringInput {
  private FileInputStream fileInputStream;
  private BufferedReader bufferedInput;
  private GZIPInputStream compressionInputStream;

  /**
   * Initialize.
   *
   * @param file source format
   * @param charset file charset
   */
  public GzipFileStringInput(File file, String charset) {
    try {
      fileInputStream = new FileInputStream(file);
      compressionInputStream = new GZIPInputStream(fileInputStream);
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
