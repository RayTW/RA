package ra.net.nio;

/**
 * Transmission package type.
 *
 * @author Ray Li
 */
public enum DataType {
  /** Text. */
  TEXT(0x0000),
  /** File. */
  FILE(0x0010);
  private final int type;

  /** File name. */
  public static final String FILE_NAME = "{\"name\":\"%s\"}";

  private DataType(int type) {
    this.type = type;
  }

  /**
   * Returns type of data.
   *
   * @return data type
   */
  public int getType() {
    return type;
  }

  /**
   * Create type use type bytes.
   *
   * @param type type
   * @return {@link DataType}
   */
  public static DataType valueOf(byte[] type) {
    return valueOf(toInt(type));
  }

  /**
   * Create type uses specific type.
   *
   * @param type type
   * @return {@link DataType}
   */
  public static DataType valueOf(int type) {
    switch (type) {
      case 0x0000:
        return TEXT;
      case 0x0010:
        return FILE;
      default:
        break;
    }
    throw new IllegalArgumentException("no match type =" + type);
  }

  /**
   * Converts the data type to int.
   *
   * @param header data type bytes
   * @return data type
   */
  public static int toInt(byte[] header) {
    return (header[0] << 8) & 0x0000ff00 | (header[1] << 0) & 0x000000ff;
  }

  /**
   * Converts the data type to bytes.
   *
   * @param data the array to copy out of
   * @param dateType data type
   */
  public static void copyToBytes(byte[] data, int dateType) {
    data[0] = (byte) ((dateType << 8) & 0x0000ff00);
    data[1] = (byte) ((dateType << 0) & 0x000000ff);
  }
}
