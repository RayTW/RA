package ra.db.record;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Type class manager.
 *
 * @author Ray Li
 */
public class JdbcTypeWrapper {

  /** Initialize. */
  public JdbcTypeWrapper() {}

  /**
   * Get corresponding Java class from {@link java.sql.Types} code.
   *
   * @param sqlType sql type
   * @return Class
   */
  public static Class<?> getClass(int sqlType) {
    if (sqlType == Types.BOOLEAN) {
      return Boolean.class;
    }
    if (sqlType == Types.BINARY
        || sqlType == Types.VARBINARY
        || sqlType == Types.LONGVARBINARY
        || sqlType == Types.BLOB) {
      return byte[].class;
    }
    if (sqlType == Types.BIT) {
      return Byte.class;
    }
    if (sqlType == Types.DATE) {
      return Date.class;
    }
    if (sqlType == Types.DOUBLE || sqlType == Types.FLOAT) {
      return Double.class;
    }
    if (sqlType == Types.BIGINT
        || sqlType == Types.INTEGER
        || sqlType == Types.SMALLINT
        || sqlType == Types.TINYINT) {
      return Long.class;
    }
    if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL) {
      return BigDecimal.class;
    }
    if (sqlType == Types.NVARCHAR
        || sqlType == Types.VARCHAR
        || sqlType == Types.LONGNVARCHAR
        || sqlType == Types.CHAR) {
      return String.class;
    }
    if (sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE) {
      return Timestamp.class;
    }

    if (sqlType == Types.ARRAY) {
      return Array.class;
    }

    return Object.class;
  }

  /**
   * Get the Java class corresponding to the value from the {@link java.sql.Types} code.
   *
   * @param result result
   * @param sqlType sql type
   * @param columnIndex index
   * @return Object
   * @throws SQLException SQLException
   */
  public static Object getValue(ResultSet result, int sqlType, int columnIndex)
      throws SQLException {
    if (sqlType == Types.BOOLEAN) {
      return result.getBoolean(columnIndex);
    }
    if (sqlType == Types.BINARY
        || sqlType == Types.VARBINARY
        || sqlType == Types.LONGVARBINARY
        || sqlType == Types.BLOB) {
      return result.getBytes(columnIndex);
    }
    if (sqlType == Types.BIT) {
      return result.getByte(columnIndex);
    }
    if (sqlType == Types.DATE) {
      return result.getDate(columnIndex);
    }
    if (sqlType == Types.DOUBLE || sqlType == Types.FLOAT) {
      return result.getDouble(columnIndex);
    }
    if (sqlType == Types.BIGINT
        || sqlType == Types.INTEGER
        || sqlType == Types.SMALLINT
        || sqlType == Types.TINYINT) {
      return result.getLong(columnIndex);
    }
    if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL) {
      return result.getBigDecimal(columnIndex);
    }
    if (sqlType == Types.NVARCHAR
        || sqlType == Types.VARCHAR
        || sqlType == Types.LONGNVARCHAR
        || sqlType == Types.CHAR) {
      return result.getString(columnIndex);
    }
    if (sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE) {
      return result.getTimestamp(columnIndex);
    }

    if (sqlType == Types.ARRAY) {
      return result.getArray(columnIndex);
    }

    return result.getObject(columnIndex);
  }
}
