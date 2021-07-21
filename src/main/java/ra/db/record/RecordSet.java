package ra.db.record;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
      default:
        throw new UnsupportedOperationException("Unsupport category = " + category);
    }
    table = newTable();
  }

  protected Map<String, List<byte[]>> newTable() {
    return new ConcurrentHashMap<String, List<byte[]>>();
  }

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
    List<byte[]> v = table.get(name);

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
    List<byte[]> v = table.get(name);

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
    List<byte[]> v = table.get(name);

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
    List<byte[]> v = table.get(name);

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
    List<byte[]> v = table.get(name);
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
   * Returns name of column.
   *
   * @param index index (range : 1 ~ < column.length)
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
  public int getLastInsertId(Statement statement) throws SQLException {
    return resultConverter.getLastInsertId(statement);
  }

  private class ResultMySql implements ResultConverter {
    /**
     * Convert the result of a query.
     *
     * @param result Result of query.
     * @throws SQLException SQLException
     */
    @Override
    public void convert(ResultSet result) throws SQLException {
      int columnNum = result.getMetaData().getColumnCount();
      String[] columnNames = new String[columnNum + 1];

      for (int i = 1; i <= columnNum; i++) {
        columnNames[i] = result.getMetaData().getColumnLabel(i);
        table.put(columnNames[i], newColumnContainer());
      }

      while (result.next()) {
        count++;
        for (int i = 1; i <= columnNum; i++) {
          List<byte[]> tmp = table.get(columnNames[i]);
          byte[] value = result.getBytes(i);
          tmp.add((value == null) ? null : value);
        }
      }
      columnName = columnNames;
    }

    /**
     * Returns last insert id.
     *
     * @param statement statement
     * @return id auto-increment id
     * @throws SQLException SQLException
     */
    @Override
    public int getLastInsertId(Statement statement) throws SQLException {
      try (ResultSet rs = statement.executeQuery("SELECT LAST_INSERT_ID() AS lastid"); ) {
        ResultMySql.this.convert(rs);
        return Integer.parseInt(field("lastid"));
      }
    }
  }

  private class ResultH2 implements ResultConverter {
    /**
     * Convert the result of a query.
     *
     * @param result Result of query.
     * @throws SQLException SQLException
     */
    @Override
    public void convert(ResultSet result) throws SQLException {
      int columnNum = result.getMetaData().getColumnCount();
      String[] columnNames = new String[columnNum + 1];

      for (int i = 1; i <= columnNum; i++) {
        columnNames[i] = result.getMetaData().getColumnLabel(i);
        table.put(columnNames[i], newColumnContainer());
      }

      while (result.next()) {
        count++;
        for (int i = 1; i <= columnNum; i++) {
          List<byte[]> tmp = table.get(columnNames[i]);
          Object obj = result.getObject(i);
          byte[] value = null;
          if (obj != null) {
            value = obj.toString().getBytes();
          }

          tmp.add((value == null) ? null : value);
        }
      }
      columnName = columnNames;
    }

    /**
     * Returns last insert id.
     *
     * @param statement statement
     * @return id auto-increment id
     * @throws SQLException SQLException
     */
    @Override
    public int getLastInsertId(Statement statement) throws SQLException {
      try (ResultSet rs = statement.executeQuery("CALL IDENTITY()"); ) {
        ResultH2.this.convert(rs);
        return Integer.parseInt(field("IDENTITY()"));
      }
    }
  }
}
