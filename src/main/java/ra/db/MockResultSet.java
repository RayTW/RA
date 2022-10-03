package ra.db;

import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.StatementImpl;
import com.mysql.cj.jdbc.result.ResultSetImpl;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.protocol.a.result.OkPacket;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
  private List<Integer> columnType;
  private List<String> columnTypeName;
  private List<String> columnLabel;
  private Map<Integer, String> columnMapping;
  private Map<String, List<byte[]>> data;
  private static final byte[] ZERO_BYTE = new byte[0];
  private int cursor;

  private MockResultSet() {
    super(
        OkPacket.parse(new NativePacketPayload(NativePacketPayload.TYPE_ID_ERROR), "utf-8"),
        (JdbcConnection) null,
        (StatementImpl) null);
  }

  /**
   * Initialize.
   *
   * @param columnLabel column label
   */
  public MockResultSet(String... columnLabel) {
    this();

    setColumnTypeLabel(null, null, Arrays.asList(columnLabel));
  }

  /**
   * Initialize.
   *
   * @param columnLabel column label
   */
  private void setColumnTypeLabel(
      List<Integer> columnType, List<String> columnTypeName, List<String> columnLabel) {
    if (columnType == null && columnLabel != null && columnLabel.size() > 0) {
      columnType = new ArrayList<Integer>(Collections.nCopies(columnLabel.size(), 0));
      columnTypeName = new ArrayList<String>(Collections.nCopies(columnLabel.size(), "NULL"));
    }

    cursor = 0;
    this.columnLabel = columnLabel;
    this.columnType = columnType;
    this.columnTypeName = columnTypeName;
    data = new ConcurrentHashMap<>();
    columnMapping = new ConcurrentHashMap<>();

    for (int i = 0; i < this.columnLabel.size(); i++) {
      String columnName = this.columnLabel.get(i);
      data.put(columnName, new CopyOnWriteArrayList<byte[]>());
      columnMapping.put(Integer.valueOf(i), columnName);
    }

    metaData =
        new RowSetMetaDataImpl() {
          private static final long serialVersionUID = 1L;

          @Override
          public int getColumnCount() throws SQLException {
            return MockResultSet.this.columnLabel.size();
          }

          @Override
          public String getColumnLabel(int columnIndex) throws SQLException {
            return MockResultSet.this.columnLabel.get(columnIndex - 1);
          }

          @Override
          public int getColumnType(int columnIndex) throws SQLException {
            return MockResultSet.this.columnType.get(columnIndex - 1);
          }

          @Override
          public String getColumnTypeName(int columnIndex) throws SQLException {
            return MockResultSet.this.columnTypeName.get(columnIndex - 1);
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

  @Override
  public String getString(int columnIndex) throws SQLException {
    String columnName = columnMapping.get(columnIndex - 1);
    List<byte[]> columnData = data.get(columnName);
    int c = cursor - 1;
    byte[] bytes = columnData.get(c);

    if (bytes != null && c < columnData.size()) {
      return new String(bytes);
    }
    return null;
  }

  @Override
  public Object getObject(int columnIndex) throws SQLException {
    String columnName = columnMapping.get(columnIndex - 1);
    List<byte[]> columnData = data.get(columnName);
    int c = cursor - 1;
    byte[] bytes = columnData.get(c);

    if (bytes != null && c < columnData.size()) {
      return bytes;
    }
    return null;
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

  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Builder.
   *
   * @author Ray Li
   */
  public static final class Builder {
    private List<Integer> columnType;
    private List<String> columnTypeName;
    private List<String> columnLabel;

    public Builder setColumnLabel(String... label) {
      columnLabel = Arrays.asList(label);
      return this;
    }

    public Builder setColumnType(Integer... type) {
      columnType = Arrays.asList(type);
      return this;
    }

    public Builder setColumnTypeName(String... name) {
      columnTypeName = Arrays.asList(name);
      return this;
    }

    /**
     * build.
     *
     * @return MockResultSet
     */
    public MockResultSet build() {
      MockResultSet obj = new MockResultSet();

      obj.setColumnTypeLabel(columnType, columnTypeName, columnLabel);

      return obj;
    }
  }
}
