package ra.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import ra.db.parameter.DatabaseParameters;

/**
 * The record of query database.
 *
 * @author Ray Li
 */
public class RecordSet implements RecordCursor {
  private int cursor = 0;
  private int count = 0;
  private Map<String, AbstractList<byte[]>> table;
  private String[] columnName;

  public RecordSet(ResultSet rs) throws SQLException {
    table = newTable();
    convert(rs);
  }

  public RecordSet() {
    convert(newTable());
  }

  protected Map<String, AbstractList<byte[]>> newTable() {
    return new Hashtable<String, AbstractList<byte[]>>();
  }

  protected AbstractList<byte[]> newColumnContainer() {
    return new Vector<byte[]>();
  }

  /**
   * An SQL statement to be sent to the database. Accept SQL statement, EX:'select...'.
   *
   * @param params Parameters of Database connect setting.
   * @param statement The object used by executing SQL statements.
   * @param sql SQL.
   * @throws SQLException Throw the SQLException, when SQL statement executing fails.
   */
  void executeQuery(DatabaseParameters params, Statement statement, String sql)
      throws SQLException {
    // Using try-close to close Statement and ResultSet. by Ray
    try (ResultSet rs = statement.executeQuery(sql); ) {
      table.clear();
      count = 0;
      cursor = 0;
      convert(params, rs);
    }
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
   * Take RecordSet new instance.
   *
   * @param map Table`s Data. Key:column name,Data:List
   */
  public static RecordSet newInstance(Map<String, AbstractList<byte[]>> map) {
    RecordSet obj = new RecordSet();

    obj.convert(map);

    return obj;
  }

  protected void convert(Map<String, AbstractList<byte[]>> table) {
    this.table = table;
    Iterator<Entry<String, AbstractList<byte[]>>> iterator = table.entrySet().iterator();
    String[] columnName = new String[table.size() + 1];
    int i = 1;
    Entry<String, AbstractList<byte[]>> entry = null;

    while (iterator.hasNext()) {
      entry = iterator.next();
      columnName[i] = entry.getKey();

      if (entry.getValue().size() > count) {
        count = entry.getValue().size();
      }
      i++;
    }

    this.columnName = columnName;
  }

  void convert(DatabaseParameters param, ResultSet rs) throws SQLException {
    if (param.getCategory() == DatabaseCategory.MYSQL) {
      convert(rs);
    } else {
      throw new UnsupportedOperationException("Unsupport DBCategory = " + param.getCategory());
    }
  }

  void convert(ResultSet rs) throws SQLException {
    int columnNum = rs.getMetaData().getColumnCount();
    String[] columnName = new String[columnNum + 1];

    for (int i = 1; i <= columnNum; i++) {
      columnName[i] = rs.getMetaData().getColumnLabel(i);
      table.put(columnName[i], newColumnContainer());
    }

    while (rs.next()) {
      count++;
      for (int i = 1; i <= columnNum; i++) {
        AbstractList<byte[]> tmp = table.get(columnName[i]);
        byte[] value = rs.getBytes(i);
        tmp.add((value == null) ? null : value);
      }
    }
    this.columnName = columnName;
  }

  /**
   * Take the column value of the earmark index.
   *
   * @param index earmark index
   */
  @Override
  public String field(int index) {
    return field(columnName[index], "utf8");
  }

  /**
   * Take the column value of the name and the current index.
   *
   * @param name column`s name
   */
  @Override
  public String field(String name) {
    return field(name, null, "");
  }

  /**
   * Take the column value of the name and the current index.
   *
   * @param name column`s name
   * @param lang The Character encoding with return value,EX:utf8
   */
  @Override
  public String field(String name, String lang) {
    return field(name, lang, "");
  }

  /**
   * Take the column value of the name and the current index， Return feedback, if the value is null.
   *
   * @param name column`s name
   * @param lang The Character encoding with return value,EX:utf8
   * @param feedback The String to return, if the value is null.
   */
  @Override
  public String field(String name, String lang, String feedback) {
    AbstractList<byte[]> v = table.get(name);

    try {
      if (v == null) {
        return feedback;
      } else {
        if (lang == null) {
          return new String(v.get(cursor));
        } else {
          return new String(v.get(cursor), lang);
        }
      }
    } catch (NullPointerException e) {
      return "";
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  /**
   * Take the column value of the name and the earmark row index， Return "", if the value is null.
   *
   * @param name column`s name
   * @param cursor row index
   */
  @Override
  public String field(String name, int cursor) {
    AbstractList<byte[]> v = table.get(name);

    if (v == null) {
      return "";
    }

    byte[] b = v.get(cursor);

    if (b == null) {
      return "";
    }

    try {
      return new String(b);
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  /**
   * Take the column value of the name and the current index， Return Object null, if the value is
   * null.
   *
   * @param name column`s name
   */
  @Override
  public String optField(String name) {
    return optField(name, null);
  }

  /**
   * Take the column value of the name and the earmark row index， Return Object null, if the value
   * is null.
   *
   * @param name column`s name
   * @param cursor row index
   */
  @Override
  public String optField(String name, int cursor) {
    AbstractList<byte[]> v = table.get(name);

    try {
      byte[] temp = v.get(cursor);
      if (temp == null) {
        return null;
      } else {
        return new String(temp);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Take the column value of the name and the current index， Return Object null, if the value is
   * null.
   *
   * @param name column`s name
   * @param lang The Character encoding with return value,EX:utf8
   */
  @Override
  public String optField(String name, String lang) {
    AbstractList<byte[]> v = table.get(name);

    try {
      byte[] temp = v.get(cursor);

      if (temp == null) {
        return null;
      }

      if (lang == null) {
        return new String(temp);
      } else {
        return new String(temp, lang);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Take the column value of the name and the current index， Return feedback, if the value is null.
   *
   * @param name column`s name
   * @param feedback The String to return, if the value is null.
   */
  @Override
  public String fieldFeedback(String name, String feedback) {
    return field(name, null, feedback);
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
    AbstractList<byte[]> v = table.get(name);
    if (v == null) {
      return null;
    } else {
      return v.get(cursor);
    }
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
        AbstractList<byte[]> v = table.get(columnName);
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
    return StreamSupport.stream(new RecordSpliterator(this, 0, count), false);
  }

  /** Return the parallel stream in SQL statement execute finish. */
  @Override
  public Stream<RowSet> parallelStream() {
    return StreamSupport.stream(new RecordSpliterator(this, 0, count), true);
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
        valueLength = field(this.columnName[i], j).length();
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
        value = optField(columnName, cursor);
        // 'null' length is 4.
        length = (value == null) ? 4 : value.length();

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
   * Record of one row.
   *
   * @author Ray Li
   */
  private static class Row implements RowSet {
    private Map<String, byte[]> data;

    Row() {
      data = new HashMap<>();
    }

    void put(String key, byte[] value) {
      data.put(key, value);
    }

    /**
     * Take the value of that column`s value as a byte array by the column`s name.
     *
     * @param columnName column`s name
     * @return the value of that column as a byte array.
     */
    @Override
    public byte[] getBlob(String columnName) {
      return data.get(columnName);
    }

    /**
     * Take the value of that column`s value as a String by the column`s name.
     *
     * @param columnName column`s name
     * @return the value of that column as a String.
     */
    @Override
    public String getString(String columnName) {
      return getString(columnName, null);
    }

    /**
     * Take the value of that column`s value as a String by character encoding and the column`s
     * name.
     *
     * @param columnName column`s name
     * @param charsetName etc. "UTF-8"
     * @return the value of that column as a String.
     */
    @Override
    public String getString(String columnName, String charsetName) {
      try {
        byte[] v = data.get(columnName);

        if (charsetName == null) {
          return (v == null) ? "" : new String(v);
        } else {
          return (v == null) ? "" : new String(v, charsetName);
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
      return "";
    }

    /**
     * Take the value of that column`s value as a short by the column`s name.
     *
     * @param columnName column`s name
     * @return the value of that column as a short.
     */
    @Override
    public short getShort(String columnName) {
      String v = getString(columnName);

      return Short.parseShort(v);
    }

    /**
     * Take the value of that column`s value as a int by the column`s name.
     *
     * @param columnName column`s name
     * @return the value of that column as an int.
     */
    @Override
    public int getInt(String columnName) {
      String v = getString(columnName);

      return Integer.parseInt(v);
    }

    /**
     * Take the value of that column`s value as a long by the column`s name.
     *
     * @param columnName column`s name
     * @return the value of that column as a long.
     */
    @Override
    public long getLong(String columnName) {
      String v = getString(columnName);

      return Long.parseLong(v);
    }

    /**
     * Take the value of that column`s value as a float by the column`s name.
     *
     * @param columnName column`s name
     * @return the value of that column as a float.
     */
    @Override
    public float getFloat(String columnName) {
      String v = getString(columnName);

      return Float.parseFloat(v);
    }

    /**
     * Take the value of that column`s value as a double by the column`s name.
     *
     * @param columnName column`s name
     * @return the value of that column as a double.
     */
    @Override
    public double getDouble(String columnName) {
      String v = getString(columnName);

      return Double.parseDouble(v);
    }

    /**
     * Take the value of that column`s value as a double by the column`s name. Using the BigDecimal
     * transform the double value.
     *
     * @param columnName column`s name
     * @return the value of that column as a {@link BigDecimal#doubleValue()}
     */
    @Override
    public double getDoubleDecima(String columnName) {
      String v = getString(columnName);

      return new BigDecimal(v).doubleValue();
    }

    /**
     * Take the value of that column`s value as a BigDecimal by the column`s name.
     *
     * @param columnName column`s name
     * @return the value of that column as a big decimal.
     */
    @Override
    public BigDecimal getBigDecimal(String columnName) {
      String v = getString(columnName);

      return new BigDecimal(v);
    }

    /**
     * Checking database null equals the value of that column`s value by the column`s name.
     *
     * @param columnName column`s name
     */
    @Override
    public boolean isNull(String columnName) {
      return data.get(columnName) == null;
    }

    void clear() {
      data.clear();
    }
  }

  private static class RecordSpliterator implements Spliterator<RowSet> {
    private final RecordSet record;
    private int origin;
    private final int fence;

    public RecordSpliterator(RecordSet record, int origin, int fence) {
      this.record = record;
      this.origin = origin;
      this.fence = fence;
    }

    @Override
    public long estimateSize() {
      return (fence - origin) / 2;
    }

    @Override
    public int characteristics() {
      return ORDERED | SIZED | IMMUTABLE | SUBSIZED;
    }

    @Override
    public boolean tryAdvance(Consumer<? super RowSet> action) {
      if (origin < fence) {
        int fieldCount = record.getFieldCount();
        Row row = new Row();
        String columnName = null;
        byte[] value = null;

        for (int j = 1; j < fieldCount; j++) {
          columnName = record.columnName[j];
          AbstractList<byte[]> v = record.table.get(columnName);

          if (v == null) {
            value = null;
          } else {
            value = v.get(origin);
          }
          row.put(columnName, value);
        }

        action.accept(row);
        origin++;

        return true;
      }

      return false;
    }

    @Override
    public Spliterator<RowSet> trySplit() {
      int lo = origin;
      int mid = ((lo + fence) >>> 1) & ~1;

      if (lo < mid) {
        origin = mid;
        return new RecordSpliterator(this.record, lo, mid);
      }

      return null;
    }
  }
}
