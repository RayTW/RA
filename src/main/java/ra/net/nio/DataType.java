package ra.net.nio;

/**
 * Transmission package type.
 *
 * @author Ray Li
 */
public enum DataType {
  TEXT(0x0000),
  ZIP(0x0010),
  FILE_NAME(0x0020),
  FILE_CONTENT(0x0030);

  private final int type;

  private DataType(int type) {
    this.type = type;
  }

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
   * 指定type創建DataType.
   *
   * @param type 要指定的資料型態
   */
  public static DataType valueOf(int type) {
    switch (type) {
      case 0x0000:
        return TEXT;
      case 0x0010:
        return ZIP;
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
