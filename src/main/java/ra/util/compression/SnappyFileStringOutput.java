package ra.util.compression;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.xerial.snappy.SnappyOutputStream;

/**
 * Use snappy to compress the output text file.
 *
 * @author Ray Li
 */
public class SnappyFileStringOutput implements StringOutput {
  private FileOutputStream fileOutputStream;
  private BufferedOutputStream bufferedOutput;
  private SnappyOutputStream compressionOutputStream;
  private String charset;

  /**
   * Initialize.
   *
   * @param file source format
   * @param charset file charset
   */
  public SnappyFileStringOutput(File file, String charset) {
    try {
      this.charset = charset;
      fileOutputStream = new FileOutputStream(file, true);
      bufferedOutput = new BufferedOutputStream(fileOutputStream);
      compressionOutputStream = new SnappyOutputStream(bufferedOutput);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void write(String str) throws IOException {
    compressionOutputStream.write(str.getBytes(charset));
  }

  @Override
  public void flush() throws IOException {
    compressionOutputStream.flush();
  }

  @Override
  public void close() throws IOException {
    compressionOutputStream.close();
  }
}
