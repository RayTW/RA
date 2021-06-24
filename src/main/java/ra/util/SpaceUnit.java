package ra.util;

/**
 * 轉換儲存空間單位.
 *
 * @author Ray Li
 */
public enum SpaceUnit {
  Bytes,
  KB,
  MB,
  GB,
  TB,
  PB;

  /**
   * For example, to convert 2048 bytes to KB, use: <tt>SpaceUnit.KB.convert(2048L,
   * SpaceUnit.Bytes)</tt>.
   *
   * @param value 容量的數量
   * @param unit 容量的單位
   */
  public long convert(long value, SpaceUnit unit) {
    int u = unit.ordinal() - ordinal();

    if (u == 0) {
      return value;
    }

    long result = value;

    if (u > 0) {
      for (int i = 0; i < u; i++) {
        result *= 1024;
      }
    } else if (u < 0) {
      u = -u;

      for (int i = 0; i < u; i++) {
        result /= 1024;
      }
    }

    return result;
  }

  /**
   * For example, to convert 2048 bytes to KB, use: <tt>SpaceUnit.KB.convert(2048L,
   * SpaceUnit.Bytes)</tt>.
   *
   * @param value 容量的數量
   * @param unit 容量的單位
   */
  public double convertDecimal(double value, SpaceUnit unit) {
    int u = unit.ordinal() - ordinal();

    if (u == 0) {
      return value;
    }

    double result = value;

    if (u > 0) {
      for (int i = 0; i < u; i++) {
        result *= 1024.0;
      }
    } else if (u < 0) {
      u = -u;

      for (int i = 0; i < u; i++) {
        result /= 1024.0;
      }
    }

    return result;
  }
}
