package ra.util.compression;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Output string with file.
 *
 * @author Ray Li
 */
public class StringFileOutput implements StringOutput {
  private FileOutputStream fileOutputStream;
  private OutputStreamWriter outputStreamWriter;
  private BufferedWriter bufferedWriter;

  /**
   * Initialize.
   *
   * @param file source format
   * @param charset file charset
   */
  public StringFileOutput(File file, String charset) {
    try {
      fileOutputStream = new FileOutputStream(file, true);
      outputStreamWriter = new OutputStreamWriter(fileOutputStream, charset);

      bufferedWriter = new BufferedWriter(outputStreamWriter);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void write(String str) throws IOException {
    bufferedWriter.write(str);
  }

  @Override
  public void flush() throws IOException {
    bufferedWriter.flush();
  }

  @Override
  public void close() throws IOException {
    bufferedWriter.close();
  }
}
