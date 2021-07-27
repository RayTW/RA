package ra.net.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONObject;
import ra.net.TransmissionEnd;

/**
 * Transmission package.
 *
 * @author Ray Li
 */
public class Data {
  private DataType dataType;
  private String title;
  private byte[] content; // etc string, ZIP file...

  /**
   * Initialize use text.
   *
   * @param text text
   */
  public Data(String text) {
    dataType = DataType.TEXT;
    title = "";
    content = text.getBytes();
  }

  /**
   * Initialize use path.
   *
   * @param path path
   */
  public Data(Path path) {
    dataType = DataType.FILE;
    title = path.getFileName().toString();
    try {
      content = Files.readAllBytes(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create data.
   *
   * @param type type
   * @param source source
   * @return Data
   */
  public static Data parse(DataType type, byte[] source) {
    return new Data(type, source);
  }

  /**
   * Initialize.
   *
   * @param type type
   * @param source source
   */
  private Data(DataType type, byte[] source) {
    dataType = type;
    StringBuilder buf = new StringBuilder();
    int offset = 0;
    char c = ' ';

    for (; ; ) {
      c = (char) source[offset];
      offset++;

      if (c == TransmissionEnd.NEW_LINE.getChar()) {
        break;
      }

      buf.append(c);
    }
    String bufString = buf.toString();

    if (type == DataType.FILE) {
      this.title = new JSONObject(bufString).optString("name");
    } else if (type == DataType.TEXT) {
      this.title = bufString;
    }
    this.content = new byte[source.length - offset];
    System.arraycopy(source, offset, content, 0, content.length);
  }

  /**
   * Returns data type.
   *
   * @return DataType
   */
  public DataType getDataType() {
    return dataType;
  }

  /**
   * Returns title.
   *
   * @return title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Returns content.
   *
   * @return content
   */
  public byte[] getContent() {
    return content;
  }

  /**
   * Returns title appends content as bytes.
   *
   * @return bytes
   */
  public byte[] toBytes() {
    String titleHeader = null;

    if (dataType == DataType.TEXT) {
      titleHeader = this.title + TransmissionEnd.NEW_LINE.getString();
    } else if (dataType == DataType.FILE) {
      titleHeader =
          String.format(DataType.FILE_NAME + TransmissionEnd.NEW_LINE.getString(), this.title);
    }

    byte[] titleBytes = titleHeader.getBytes();
    byte[] result = new byte[titleBytes.length + content.length];
    System.arraycopy(titleBytes, 0, result, 0, titleBytes.length);
    System.arraycopy(content, 0, result, titleBytes.length, content.length);

    return result;
  }

  @Override
  public String toString() {
    return String.format("type=%s, title=%s, content.length=%d", dataType, title, content.length);
  }
}
