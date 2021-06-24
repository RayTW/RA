package ra.net.nio;

/**
 * Transmission package.
 *
 * @author Ray Li
 */
public class Data {
  private DataType dataType;
  private byte[] raw; // etc string, ZIP file...

  public Data(DataType type, byte[] raw) {
    dataType = type;
    this.raw = raw;
  }

  public DataType getDataType() {
    return dataType;
  }

  public byte[] getRaw() {
    return raw;
  }

  @Override
  public String toString() {
    return String.format("type=%s, data=%d", dataType, raw.length);
  }
}
