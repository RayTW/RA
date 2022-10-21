package ra.db;

import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.StatementImpl;
import com.mysql.cj.jdbc.result.ResultSetImpl;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.protocol.a.result.OkPacket;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.sql.rowset.RowSetMetaDataImpl;
import ra.db.record.JdbcTypeWrapper;

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
  private Map<String, List<Object>> data;
  private int cursor;

  /** Initialize. */
  private MockResultSet() {
    super(
        OkPacket.parse(new NativePacketPayload(NativePacketPayload.TYPE_ID_ERROR), "utf-8"),
        (JdbcConnection) null,
        (StatementImpl) null);
  }

  /**
   * Initialize.
   *
   * @param columnType column type
   * @param columnLabel column label
   */
  public MockResultSet(List<Integer> columnType, List<String> columnLabel) {
    this();

    setColumnTypeLabel(columnType, typeParseToName(columnType), columnLabel);
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
      data.put(columnName, new CopyOnWriteArrayList<Object>());
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

  @Override
  public String getString(int columnIndex) throws SQLException {
    return checkedCastToString(columnIndex);
  }

  @Override
  public long getLong(int columnIndex) throws SQLException {
    return Long.parseLong(checkedCastToString(columnIndex));
  }

  @Override
  public boolean getBoolean(int columnIndex) throws SQLException {
    return Boolean.parseBoolean(checkedCastToString(columnIndex));
  }

  @Override
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    return new BigDecimal(checkedCastToString(columnIndex));
  }

  @Override
  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    return Timestamp.valueOf(checkedCastToString(columnIndex));
  }

  @Override
  public Array getArray(int columnIndex) throws SQLException {
    Object ret = getObject(columnIndex);

    if (checkedCastType(columnIndex, Array.class)) {
      return (Array) ret;
    }

    return null;
  }

  /**
   * Returns the data byte of the specified index field.
   *
   * @param columnIndex column index
   */
  @Override
  public byte[] getBytes(int columnIndex) throws SQLException {
    Object ret = getObject(columnIndex);

    if (checkedCastType(columnIndex, byte[].class)) {
      return (byte[]) ret;
    }

    return ret == null ? null : ret.toString().getBytes();
  }

  @Override
  public byte getByte(int columnIndex) throws SQLException {
    Object ret = getObject(columnIndex);

    if (checkedCastType(columnIndex, Byte.class)) {
      return (byte) ret;
    }

    return 0;
  }

  @Override
  public double getDouble(int columnIndex) throws SQLException {
    return Double.parseDouble(checkedCastToString(columnIndex));
  }

  @Override
  public Date getDate(int columnIndex) throws SQLException {
    return Date.valueOf(checkedCastToString(columnIndex));
  }

  /**
   * Returns the data from the specified index field.
   *
   * @param columnIndex column index
   */
  @Override
  public Object getObject(int columnIndex) throws SQLException {
    String columnName = columnMapping.get(columnIndex - 1);
    List<Object> columnData = data.get(columnName);
    int c = cursor - 1;

    if (c < columnData.size()) {
      Object ret = columnData.get(c);

      return ret;
    }

    return null;
  }

  private String checkedCastToString(int columnIndex) throws SQLException {
    Object ret = getObject(columnIndex);

    return ret == null ? null : String.valueOf(ret);
  }

  private boolean checkedCastType(int columnIndex, Class<?> checkedClass) throws SQLException {
    int sqlType = metaData.getColumnType(columnIndex);
    Class<?> cls = JdbcTypeWrapper.getClass(sqlType);

    return checkedClass.isAssignableFrom(cls);
  }

  /**
   * Add specified field data.
   *
   * @param columnName column name
   * @param value value
   */
  public void addValue(String columnName, Object value) {
    List<Object> list = data.get(columnName);

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

  static List<String> typeParseToName(List<Integer> types) {
    List<String> names = new CopyOnWriteArrayList<>();

    types.forEach(
        type -> {
          Field[] fields = Types.class.getFields();

          for (Field f : fields) {

            try {
              if (type == f.getInt(f)) {
                names.add(f.getName());
                break;
              }
            } catch (IllegalArgumentException e) {
              e.printStackTrace();
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          }
        });

    return names;
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

    /**
     * build.
     *
     * @return MockResultSet
     */
    public MockResultSet build() {
      if (columnType == null) {
        columnType = Collections.emptyList();
      }

      columnTypeName = typeParseToName(columnType);

      if (columnLabel == null) {
        columnLabel = Collections.emptyList();
      }

      MockResultSet obj = new MockResultSet();

      obj.setColumnTypeLabel(columnType, columnTypeName, columnLabel);

      return obj;
    }
  }
}
