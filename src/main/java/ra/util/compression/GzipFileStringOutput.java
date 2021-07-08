package ra.util.compression;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * Use gzip to compress the output text file.
 *
 * @author Ray Li
 */
public class GzipFileStringOutput implements StringOutput {
  private FileOutputStream fileOutputStream;
  private BufferedOutputStream bufferedOutput;
  private GZIPOutputStream compressionOutputStream;
  private String charset;

  /**
   * Initialize.
   *
   * @param file source format
   * @param charset file charset
   * @throws IOException IOException
   */
  public GzipFileStringOutput(File file, String charset) throws IOException {
    this.charset = charset;
    fileOutputStream = new FileOutputStream(file, true);
    bufferedOutput = new BufferedOutputStream(fileOutputStream);
    compressionOutputStream = new GZIPOutputStream(bufferedOutput);
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
