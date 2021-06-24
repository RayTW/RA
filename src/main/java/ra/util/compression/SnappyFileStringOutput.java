package ra.util.compression;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.xerial.snappy.SnappyOutputStream;

/**
 * 採用snappy方式壓縮輸出文字檔案.
 *
 * @author Ray Li
 */
public class SnappyFileStringOutput implements StringOutput {
  private FileOutputStream fileOutputStream;
  private BufferedOutputStream bufferedOutput;
  private SnappyOutputStream compressionOutputStream;
  private String charset;

  /**
   * .
   *
   * @param file 要壓縮的檔案
   * @param charset 檔案編碼格式
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
