package ra.db.record;

import com.mysql.cj.util.StringUtils;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import ra.db.DatabaseCategory;
import ra.db.Row;
import ra.db.RowSet;

/**
 * The record of query database.
 *
 * @author Ray Li
 */
public class RecordSet implements Record {
  private int cursor = 0;
  private int count = 0;
  private Map<String, List<byte[]>> table;
  private String[] columnName;
  private ResultConverter resultConverter;

  /**
   * Initialize.
   *
   * @param category database mode
   */
  public RecordSet(DatabaseCategory category) {
    switch (category) {
      case MYSQL:
        resultConverter = new ResultMySql();
        break;
      case H2:
        resultConverter = new ResultH2();
        break;
      case BIGQUERY:
        resultConverter = new ResultBigQuery();
        break;
      case SPANNER:
        resultConverter = new ResultSpanner();
        break;
      default:
        throw new UnsupportedOperationException("Unsupport category = " + category);
    }
    table = newTable();
  }

  /**
   * Create record table.
   *
   * @return map
   */
  protected Map<String, List<byte[]>> newTable() {
    return new ConcurrentHashMap<String, List<byte[]>>();
  }

  /**
   * Create column container.
   *
   * @return list
   */
  protected List<byte[]> newColumnContainer() {
    return Collections.synchronizedList(new ArrayList<byte[]>());
  }

  /**
   * Take count in the table`s column.
   *
   * @return count in the table`s column.
   */
  @Override
  public int getFieldCount() {
    return columnName.length;
  }

  @Override
  public void fieldNames(Consumer<String> consumer) {
    Objects.requireNonNull(consumer, "consumer must not be null");

    for (int i = 1; i < columnName.length; i++) {
      consumer.accept(columnName[i]);
    }
  }

  /**
   * If the query database specifies a field name and its value is null, it will return null.
   *
   * @param name Field name
   * @return boolean
   */
  @Override
  public boolean isNull(String name) {
    List<byte[]> v = table.get(name);

    if (v == null) {
      return true;
    }

    byte[] b = v.get(cursor);

    if (b == null) {
      return true;
    }

    return false;
  }

  /**
   * Convert the result of a query.
   *
   * @param result Result of query.
   * @throws SQLException SQLException
   */
  @Override
  public void convert(ResultSet result) throws SQLException {
    resultConverter.convert(result);
  }

  /**
   * Take the column value of the earmark index.
   *
   * @param index earmark index
   */
  @Override
  public String field(int index) {
    return field(columnName[index]);
  }

  /**
   * Take the column value of the name and the earmark row index， Return "", if the value is null.
   *
   * @param name column`s name
   * @param cursor row index
   */
  @Override
  public String field(String name, int cursor) {
    byte[] b = fieldBytes(name, cursor);

    if (b == null) {
      return null;
    }

    return new String(b);
  }

  /**
   * Take the column value of the name and the current index， Return Object null, if the value is
   * null.
   *
   * @param name column`s name
   * @return String || null
   */
  @Override
  public String field(String name) {
    byte[] v = fieldBytes(name);

    if (v == null) {
      return null;
    }
    return new String(v);
  }

  /**
   * Take the column value of the name and the current index， Return Object null, if the value is
   * null.
   *
   * @param name column`s name
   * @return byte[] || null
   */
  @Override
  public byte[] fieldBytes(String name) {
    return fieldBytes(name, cursor);
  }

  private byte[] fieldBytes(String name, int c) {
    List<byte[]> v = table.get(name);

    if (v == null) {
      return null;
    }

    return v.get(c);
  }

  @Override
  public long fieldLong(String name) {
    String ret = field(name);

    return Long.parseLong(ret);
  }

  @Override
  public int fieldInt(String name) {
    String ret = field(name);

    return Integer.parseInt(ret);
  }

  @Override
  public short fieldShort(String name) {
    String ret = field(name);

    return Short.parseShort(ret);
  }

  @Override
  public float fieldFloat(String name) {
    String ret = field(name);

    return Float.parseFloat(ret);
  }

  @Override
  public double fieldDouble(String name) {
    String ret = field(name);

    return Double.parseDouble(ret);
  }

  @Override
  public BigDecimal fieldBigDecimal(String name) {
    String ret = field(name);

    return new BigDecimal(ret);
  }

  @Override
  public double fieldBigDecimalDouble(String name) {
    String ret = field(name);

    return new BigDecimal(ret).doubleValue();
  }

  /** Add one up the current row index. */
  @Override
  public void next() {
    cursor++;
  }

  /** Subtract one from the current row index. */
  @Override
  public void previous() {
    cursor--;
  }

  /**
   * Change the current row index.
   *
   * @param index Earmark row index.
   */
  @Override
  public void move(int index) {
    cursor = index - 1;
  }

  /** Change the current row index to the first index. */
  @Override
  public void first() {
    cursor = 0;
  }

  /** Change the current row index to the end index. */
  @Override
  public void end() {
    cursor = count - 1;
  }

  /** Is the current row index equal first index. */
  @Override
  public boolean isBof() {
    return (cursor < 0) || (count == 0);
  }

  /** is the current row index equal end index. */
  @Override
  public boolean isEof() {
    return cursor > (count - 1);
  }

  /**
   * Take the row count in SQL statement execute finish.
   *
   * @return row count
   */
  @Override
  public int getRecordCount() {
    return count;
  }

  /**
   * Take the row data by row data.
   *
   * <p>Execute situation like the program show
   *
   * <pre>
   * RecordSet record = ...
   *
   * while(!record.isEOF()){
   *
   *   //do get record data
   *
   *   record.next();
   * }
   * </pre>
   *
   * @param action Take the row data.The instance RowSet would be reused.
   */
  @Override
  public void forEach(Consumer<RowSet> action) {
    Objects.requireNonNull(action);
    int count = this.count;
    int fieldCount = getFieldCount();
    Row row = new Row();
    String columnName = null;
    byte[] value = null;

    for (int i = 0; i < count; i++) {
      for (int j = 1; j < fieldCount; j++) {
        columnName = this.columnName[j];
        List<byte[]> v = table.get(columnName);
        if (v == null) {
          value = null;
        } else {
          value = v.get(i);
        }
        row.put(columnName, value);
      }
      action.accept(row);
      row.clear();
    }
  }

  /** Return the serialization stream in SQL statement execute finish. */
  @Override
  public Stream<RowSet> stream() {
    return StreamSupport.stream(new RecordSpliterator<RecordSet>(this, 0, count), false);
  }

  /** Return the parallel stream in SQL statement execute finish. */
  @Override
  public Stream<RowSet> parallelStream() {
    return StreamSupport.stream(new RecordSpliterator<RecordSet>(this, 0, count), true);
  }

  @Override
  public String toString() {
    int recordCount = count;
    String columnName = null;
    int[] columnsMaxLength = new int[this.columnName.length];
    int valueLength = 0;
    int nullLength = 4;
    StringBuilder lackBuilder = new StringBuilder();
    java.util.function.BiFunction<Integer, StringBuilder, String> doLack =
        (lackLength, builder) -> {
          builder.setLength(0);

          for (int i = 0; i < lackLength; i++) {
            builder.append(' ');
          }
          return builder.toString().intern();
        };

    // Take all column`s name length.
    for (int i = 1; i < this.columnName.length; i++) {
      columnsMaxLength[i] = this.columnName[i].length();
    }

    // Take the greater of column`s data length.
    for (int i = 1; i < columnsMaxLength.length; i++) {
      for (int j = 0; j < recordCount; j++) {
        String value = field(this.columnName[i], j);

        valueLength = value == null ? nullLength : value.length();

        if (columnsMaxLength[i] < valueLength) {
          columnsMaxLength[i] = valueLength;
        }
      }
    }

    // Fill in the column`s name or the insufficient length of the data with blank characters,
    // when print data.
    StringBuilder buf = new StringBuilder();
    buf.append(System.lineSeparator());

    for (int i = 1; i < this.columnName.length; i++) {
      if (i == 1) {
        buf.append("|");
      }
      buf.append(this.columnName[i]);
      buf.append(doLack.apply(columnsMaxLength[i] - this.columnName[i].length(), lackBuilder));
      buf.append("|");
    }
    buf.append(System.lineSeparator());

    String value = null;
    int length = 0;

    for (int cursor = 0; cursor < recordCount; cursor++) {
      for (int i = 1; i < this.columnName.length; i++) {
        columnName = this.columnName[i];
        if (i == 1) {
          buf.append("|");
        }
        value = field(columnName, cursor);

        // 'null' length is 4.
        length = (value == null) ? nullLength : value.length();

        buf.append(value);
        buf.append(doLack.apply(columnsMaxLength[i] - length, lackBuilder));
        buf.append("|");
      }
      buf.append(System.lineSeparator());
    }

    return buf.toString();
  }

  /** Clear the catch data. */
  @Override
  public void close() {
    table.clear();
    count = 0;
    cursor = 0;
  }

  /**
   * Returns name of column.
   *
   * @param index index (range : 1 ~ column.length - 1)
   */
  @Override
  public String getColumnName(int index) {
    return columnName[index];
  }

  /**
   * Returns column.
   *
   * @param columnName columnName
   */
  @Override
  public List<byte[]> getColumn(String columnName) {
    return table.get(columnName);
  }

  /**
   * Returns last insert id.
   *
   * @param statement statement
   * @return id auto-increment id
   * @throws SQLException SQLException
   */
  @Override
  public LastInsertId getLastInsertId(Statement statement) throws SQLException {
    return resultConverter.getLastInsertId(statement);
  }

  private boolean isBinaryType(int n) {
    return n == Types.BINARY || n == Types.VARBINARY || n == Types.LONGVARBINARY;
  }

  private abstract class AbstractResultConverter implements ResultConverter {
    /**
     * Convert the result of a query.
     *
     * @param result Result of query.
     * @throws SQLException SQLException
     */
    @Override
    public void convert(ResultSet result) throws SQLException {
      int columnNum = result.getMetaData().getColumnCount();
      String[] name = new String[columnNum + 1];
      int[] types = new int[columnNum + 1];

      for (int i = 1; i <= columnNum; i++) {
        name[i] = result.getMetaData().getColumnLabel(i);
        types[i] = result.getMetaData().getColumnType(i);
        table.put(name[i], newColumnContainer());
      }

      while (result.next()) {
        count++;
        for (int i = 1; i <= columnNum; i++) {
          List<byte[]> tmp = table.get(name[i]);
          byte[] value = convertElement(result, i, types[i]);
          tmp.add((value == null) ? null : value);
        }
      }
      columnName = name;
    }

    public abstract byte[] convertElement(ResultSet result, int columnIndex, int type)
        throws SQLException;
  }

  private class ResultMySql extends AbstractResultConverter {

    /**
     * Convert the result of a query.
     *
     * @param result Result of query.
     * @param columnIndex column index
     * @param type type
     * @throws SQLException SQLException
     */
    @Override
    public byte[] convertElement(ResultSet result, int columnIndex, int type) throws SQLException {
      return result.getBytes(columnIndex);
    }

    /**
     * Returns last insert id.
     *
     * @param statement statement
     * @return id auto-increment id
     * @throws SQLException SQLException
     */
    @Override
    public LastInsertId getLastInsertId(Statement statement) throws SQLException {
      try (ResultSet rs = statement.executeQuery("SELECT LAST_INSERT_ID() AS lastid"); ) {
        super.convert(rs);
        return new LastInsertId(field("lastid"));
      }
    }
  }

  private class ResultH2 extends AbstractResultConverter {

    /**
     * Convert the result of a query.
     *
     * @param result Result of query.
     * @param columnIndex column index
     * @param type type
     * @throws SQLException SQLException
     */
    @Override
    public byte[] convertElement(ResultSet result, int columnIndex, int type) throws SQLException {
      if (isBinaryType(type) || type == Types.BLOB) {
        return result.getBytes(columnIndex);
      }

      Object obj = result.getObject(columnIndex);

      if (obj != null) {
        if (obj instanceof byte[]) {
          return (byte[]) obj;
        }
        return obj.toString().getBytes();
      }

      return null;
    }

    /**
     * Returns last insert id.
     *
     * @param statement statement
     * @return id auto-increment id
     * @throws SQLException SQLException
     */
    @Override
    public LastInsertId getLastInsertId(Statement statement) throws SQLException {
      try (ResultSet rs = statement.getGeneratedKeys(); ) {
        super.convert(rs);
        String lastId = field(1);

        if (StringUtils.isNullOrEmpty(lastId)) {
          throw new SQLWarning("Failed to get last insert ID.");
        }
        return new LastInsertId(lastId);
      }
    }
  }

  private class ResultBigQuery extends AbstractResultConverter {

    /**
     * Convert the result of a query.
     *
     * @param result Result of query.
     * @param columnIndex column index
     * @param type type
     * @throws SQLException SQLException
     */
    @Override
    public byte[] convertElement(ResultSet result, int columnIndex, int type) throws SQLException {
      if (isBinaryType(type)) {
        return result.getBytes(columnIndex);
      }

      String value = result.getString(columnIndex);

      if (value != null) {
        return value.getBytes();
      }
      return null;
    }

    /**
     * Unsupported.
     *
     * @param statement statement
     * @return id auto-increment id
     * @throws SQLException SQLException
     */
    @Override
    public LastInsertId getLastInsertId(Statement statement) throws SQLException {
      throw new UnsupportedOperationException(
          "BigQuery does not support getting the last ID from a query.");
    }
  }

  private class ResultSpanner extends AbstractResultConverter {
    /**
     * Convert the result of a query.
     *
     * @param result Result of query.
     * @param columnIndex column index
     * @param type type
     * @throws SQLException SQLException
     */
    @Override
    public byte[] convertElement(ResultSet result, int columnIndex, int type) throws SQLException {
      if (isBinaryType(type) || type == Types.BLOB) {

        return result.getBytes(columnIndex);
      }

      Object obj = result.getObject(columnIndex);

      if (obj != null) {
        if (obj instanceof byte[]) {
          return (byte[]) obj;
        }
        return obj.toString().getBytes();
      }

      return null;
    }

    /**
     * Unsupported.
     *
     * @param statement statement
     * @return id auto-increment id
     * @throws SQLException SQLException
     */
    @Override
    public LastInsertId getLastInsertId(Statement statement) throws SQLException {
      throw new UnsupportedOperationException("There is no auto-increment capability in Spanner.");
    }
  }
}
