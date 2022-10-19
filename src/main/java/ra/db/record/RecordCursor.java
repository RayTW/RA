package ra.db.record;

import java.io.Closeable;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * The result of query when executes SQL statement via statement executor.
 *
 * @author Ray Li
 */
public interface RecordCursor extends Closeable {
  /**
   * Get count of table fields.
   *
   * @return field count
   */
  public int getFieldCount();

  /**
   * If the query database specifies a field name and its value is null, it will return null.
   *
   * @param name Field name
   * @return boolean
   */
  public boolean isNull(String name);

  /**
   * Get name of table fields.
   *
   * @param action Consumer
   */
  public void fieldNames(Consumer<String> action);

  /**
   * Get value of specific fields.
   *
   * @param index An index returns a value of position.
   * @return value
   */
  public String field(int index);

  /**
   * Use field name returns a value.
   *
   * @param name Field name
   * @return value
   */
  public String field(String name);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   * @param name Field name
   * @return bytes
   */
  public byte[] fieldBytes(String name);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   * @param name Field name
   * @return bytes
   */
  public long fieldLong(String name);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   * @param name Field name
   * @return bytes
   */
  public int fieldInt(String name);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   * @param name Field name
   * @return bytes
   */
  public float fieldFloat(String name);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   * @param name Field name
   * @return bytes
   */
  public double fieldDouble(String name);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   * @param name Field name
   * @return bytes
   */
  public BigDecimal fieldBigDecimal(String name);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   * @param name Field name
   * @param castClass Cast class
   * @return List
   */
  public <T> List<T> fieldArray(String name, Class<T[]> castClass);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   * @param name Field name
   * @return Object
   */
  public Object fieldObject(String name);

  /** Next row. */
  public void next();

  /** Previous row. */
  public void previous();

  /**
   * Move to index.
   *
   * @param index index
   */
  public void move(int index);

  /** First record. */
  public void first();

  /** Last record. */
  public void end();

  /**
   * Verify whether first record.
   *
   * @return If first row returns true.
   */
  public boolean isBof();

  /**
   * Verify whether end of the record.
   *
   * @return If last row returns true.
   */
  public boolean isEof();

  /**
   * Get count of record.
   *
   * @return count of record
   */
  public int getRecordCount();

  /**
   * Get each row data from the record.
   *
   * @param action Row of record
   */
  public void forEach(Consumer<RowSet> action);

  /**
   * Returns ordered stream of the number of data after the query is completed.
   *
   * @return Stream
   */
  public Stream<RowSet> stream();

  /**
   * Returns parallel stream of the number of data after the query is completed.
   *
   * @return Stream
   */
  public Stream<RowSet> parallelStream();
}
