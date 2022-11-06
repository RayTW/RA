package ra.db.record;

import com.mysql.cj.util.StringUtils;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import ra.db.DatabaseCategory;
import ra.exception.RaSqlException;

/**
 * The record of query database.
 *
 * @author Ray Li
 */
public class RecordSet implements Record {
  private int cursor = 0;
  private int count = 0;
  private Map<String, List<Object>> table;
  private String[] columnName;
  private Map<String, Integer> columnTypes;
  private DatabaseCategory dbCategory;

  /**
   * Initialize.
   *
   * @param category database mode
   */
  public RecordSet(DatabaseCategory category) {
    dbCategory = category;
    columnTypes = new ConcurrentHashMap<>();
    table = newTable();
  }

  /**
   * Create record table.
   *
   * @return map
   */
  protected Map<String, List<Object>> newTable() {
    return new ConcurrentHashMap<>();
  }

  /**
   * Create column container.
   *
   * @return list
   */
  protected List<Object> newColumnContainer() {
    return Collections.synchronizedList(new ArrayList<Object>());
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
    Object obj = getObject(name, cursor);

    return obj == null;
  }

  /**
   * Convert the result of a query.
   *
   * @param result Result of query.
   * @throws SQLException SQLException
   */
  @Override
  public void convert(ResultSet result) throws SQLException {
    ResultSetMetaData meta = result.getMetaData();
    int columnNum = meta.getColumnCount();
    String[] name = new String[columnNum + 1];
    int[] types = new int[columnNum + 1];

    for (int i = 1; i <= columnNum; i++) {
      name[i] = meta.getColumnLabel(i);
      types[i] = meta.getColumnType(i);
      columnTypes.put(name[i], types[i]);

      table.put(name[i], newColumnContainer());
    }

    while (result.next()) {
      count++;
      for (int i = 1; i <= columnNum; i++) {
        List<Object> tmp = table.get(name[i]);
        Object value = JdbcTypeWrapper.getValue(result, types[i], i);

        tmp.add(value);
      }
    }
    columnName = name;
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
   * Take the column value of the name and the current index. Returns null if the value in the
   * database is null.
   *
   * @param name column`s name
   * @return String || null
   */
  @Override
  public String field(String name) {
    Object ret = getObject(name, cursor);

    if (ret == null) {
      return null;
    }

    return String.valueOf(ret);
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
    Object obj = getObject(name, cursor);

    if (obj == null) {
      return null;
    }
    if (obj instanceof byte[]) {
      return (byte[]) obj;
    }

    throw new RaSqlException("fieldName '" + name + "' can't cast to byte[].");
  }

  @Override
  public long fieldLong(String name) {
    Object obj = getObject(name, cursor);

    if (obj == null) {
      throw new RaSqlException(
          "The value is null, fieldName '" + name + "' cannot be converted to a Long.");
    }

    if (obj instanceof Long) {
      return (long) obj;
    }

    return Long.parseLong(obj.toString());
  }

  @Override
  public int fieldInt(String name) throws NumberFormatException {
    Object obj = getObject(name, cursor);

    if (obj == null) {
      throw new RaSqlException(
          "The value is null, fieldName '" + name + "' cannot be converted to an Integer.");
    }

    return Integer.parseInt(obj.toString());
  }

  @Override
  public float fieldFloat(String name) {
    Object obj = getObject(name, cursor);

    if (obj == null) {
      throw new RaSqlException(
          "The value is null, fieldName '" + name + "' cannot be converted to a Float.");
    }

    return Float.parseFloat(obj.toString());
  }

  @Override
  public double fieldDouble(String name) {
    Object obj = getObject(name, cursor);

    if (obj == null) {
      throw new RaSqlException(
          "The value is null, fieldName '" + name + "' cannot be converted to a Double.");
    }

    if (obj instanceof Double) {
      return (double) obj;
    }

    return Double.parseDouble(obj.toString());
  }

  @Override
  public BigDecimal fieldBigDecimal(String name) {
    Object obj = getObject(name, cursor);

    if (obj == null) {
      return null;
    }

    if (obj instanceof BigDecimal) {
      return (BigDecimal) obj;
    }

    return new BigDecimal(obj.toString());
  }

  @Override
  public <T> List<T> fieldArray(String name, Class<T[]> castClass) {
    Array obj = (Array) getObject(name, cursor);

    if (obj == null) {
      return null;
    }

    try {
      Object array = obj.getArray();

      return Arrays.asList(castClass.cast(array));
    } catch (Exception e) {
      throw new RaSqlException("fieldName '" + name + "' can't cast to " + castClass + ".", e);
    }
  }

  @Override
  public Object fieldObject(String name) {
    return getObject(name, cursor);
  }

  private Object getObject(String fieldName, int c) {
    if (fieldName == null) {
      throw new RaSqlException("fieldName can't be null");
    }

    List<Object> v = table.get(fieldName);

    if (v == null) {
      return null;
    }

    return v.get(c);
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
    Row row = new Row(this);

    for (int i = 0; i < count; i++) {
      action.accept(row);
      next();
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
        String value = String.valueOf(getObject(this.columnName[i], j));

        valueLength = value.length();

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
        value = String.valueOf(getObject(columnName, cursor));

        // 'null' length is 4.
        length = value.length();

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
   * Returns last insert id.
   *
   * @param statement statement
   * @return id auto-increment id
   * @throws SQLException SQLException
   */
  @Override
  public LastInsertId getLastInsertId(Statement statement) throws SQLException {
    if (dbCategory == DatabaseCategory.MYSQL) {
      return getLastInsertIdFromMySql(statement);
    }
    if (dbCategory == DatabaseCategory.H2) {
      return getLastInsertIdFromH2(statement);
    }
    throw new UnsupportedOperationException(
        "There is no auto-increment capability in " + dbCategory + ".");
  }

  private LastInsertId getLastInsertIdFromMySql(Statement statement) throws SQLException {
    try (ResultSet rs = statement.executeQuery("SELECT LAST_INSERT_ID() AS lastid"); ) {
      this.convert(rs);
      return new LastInsertId(field("lastid"));
    }
  }

  private LastInsertId getLastInsertIdFromH2(Statement statement) throws SQLException {
    try (ResultSet rs = statement.getGeneratedKeys()) {
      this.convert(rs);

      String lastId = field(1);

      if (StringUtils.isNullOrEmpty(lastId)) {
        throw new SQLWarning("Failed to get last insert ID.");
      }
      return new LastInsertId(lastId);
    }
  }
}
