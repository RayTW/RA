package ra.util;

/**
 * Convert storage space unit.
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
   * For example, to convert 2048 bytes to KB, use: <b>SpaceUnit.KB.convert(2048L,
   * SpaceUnit.Bytes)</b>.
   *
   * @param value space amount
   * @param unit space unit
   * @return convert amount
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
   * For example, to convert 2048 bytes to KB, use: <b>SpaceUnit.KB.convert(2048L,
   * SpaceUnit.Bytes)</b>.
   *
   * @param value space amount
   * @param unit space unit
   * @return convert amount
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
