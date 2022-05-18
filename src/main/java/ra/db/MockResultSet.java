package ra.db;

import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.StatementImpl;
import com.mysql.cj.jdbc.result.ResultSetImpl;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.protocol.a.result.OkPacket;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.sql.rowset.RowSetMetaDataImpl;

/**
 * Mock query result.
 *
 * @author Ray Li
 */
public class MockResultSet extends ResultSetImpl {
  private RowSetMetaDataImpl metaData;
  private String[] columnLabel;
  private Map<Integer, String> columnMapping;
  private Map<String, List<byte[]>> data;
  private static final byte[] ZERO_BYTE = new byte[0];
  private int cursor;

  /**
   * Initialize.
   *
   * @param columnLabel column label
   */
  public MockResultSet(String... columnLabel) {
    super(
        OkPacket.parse(new NativePacketPayload(NativePacketPayload.TYPE_ID_ERROR), "utf-8"),
        (JdbcConnection) null,
        (StatementImpl) null);
    cursor = 0;
    this.columnLabel = columnLabel;
    data = new ConcurrentHashMap<>();
    columnMapping = new ConcurrentHashMap<>();

    for (int i = 0; i < this.columnLabel.length; i++) {
      String columnName = this.columnLabel[i];
      data.put(columnName, new CopyOnWriteArrayList<byte[]>());
      columnMapping.put(Integer.valueOf(i), columnName);
    }

    metaData =
        new RowSetMetaDataImpl() {
          private static final long serialVersionUID = 1L;

          @Override
          public int getColumnCount() throws SQLException {
            int r = MockResultSet.this.columnLabel.length;
            return r;
          }

          @Override
          public String getColumnLabel(int column) throws SQLException {
            return MockResultSet.this.columnLabel[column - 1];
          }
        };
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return metaData;
  }

  @Override
  public boolean next() {
    if (cursor + 1 > size()) {
      return false;
    }
    cursor++;
    return true;
  }

  /**
   * Returns the data byte of the specified index field.
   *
   * @param columnIndex column index
   */
  @Override
  public byte[] getBytes(int columnIndex) {
    return getBytes(columnIndex - 1, cursor - 1);
  }

  /**
   * Returns the data from the specified index field.
   *
   * @param columnIndex column index
   * @param cursor cursor
   */
  private byte[] getBytes(int columnIndex, int cursor) {
    String columnName = columnMapping.get(columnIndex);
    List<byte[]> columnData = data.get(columnName);

    if (cursor < columnData.size()) {
      return columnData.get(cursor);
    }

    return ZERO_BYTE;
  }

  /**
   * Add specified field data.
   *
   * @param columnName column name
   * @param value value
   */
  public void addValue(String columnName, String value) {
    addValue(columnName, value == null ? null : value.getBytes());
  }

  /**
   * Add specified field data.
   *
   * @param columnName column name
   * @param value value
   */
  public void addValue(String columnName, long value) {
    addValue(columnName, String.valueOf(value).getBytes());
  }

  /**
   * Add specified field data.
   *
   * @param columnName column name
   * @param value value
   */
  public void addValue(String columnName, short value) {
    addValue(columnName, String.valueOf(value).getBytes());
  }

  /**
   * Add specified field data.
   *
   * @param columnName column name
   * @param value value
   */
  public void addValue(String columnName, float value) {
    addValue(columnName, String.valueOf(value).getBytes());
  }

  /**
   * Add specified field data.
   *
   * @param columnName column name
   * @param value value
   */
  public void addValue(String columnName, double value) {
    addValue(columnName, String.valueOf(value).getBytes());
  }

  /**
   * Add specified field data.
   *
   * @param columnName column name
   * @param value value
   */
  public void addValue(String columnName, BigDecimal value) {
    addValue(columnName, value == null ? ZERO_BYTE : value.toString().getBytes());
  }

  /**
   * Add specified field data.
   *
   * @param columnName column name
   * @param value value
   */
  public void addValue(String columnName, byte[] value) {
    List<byte[]> list = data.get(columnName);

    if (list == null) {
      throw new IllegalArgumentException(
          "The specified key does not exist ,columnName = '" + columnName + "' ");
    }
    list.add(value);
  }

  private int size() {
    return data.values().stream().map(list -> list.size()).reduce(Math::max).orElse(0);
  }
}
