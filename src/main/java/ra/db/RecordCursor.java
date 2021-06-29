package ra.db;

import java.io.Closeable;
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
   * Use field name returns a value.
   *
   * @param name Field name
   * @param charset Charset, etc : utf8
   * @return value
   */
  public String field(String name, String charset);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   *
   * @param name Field name
   * @param lang Charset, etc : utf8
   * @param feedback Return the feedback when if get value of the field is null.
   * @return value
   */
  public String field(String name, String lang, String feedback);

  /**
   * Get value by specified name.
   *
   * @param name Field name
   * @param cursor Record cursor
   * @return value
   */
  public String field(String name, int cursor);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   * @param name Field name
   * @return value
   */
  public String optField(String name);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   * @param name Field name
   * @param cursor Record cursor
   * @return value
   */
  public String optField(String name, int cursor);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   * @param name Field name
   * @param lang Record cursor
   * @return value
   */
  public String optField(String name, String lang);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   * @param name Field name
   * @param feedback Return the feedback when if get value of the field is null.
   * @return value
   */
  public String fieldFeedback(String name, String feedback);

  /**
   * Get value by specified field name. If field value is null will return null.
   *
   * @param name Field name
   * @return bytes
   */
  public byte[] fieldBytes(String name);

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
   * Return ordered stream of the number of data after the query is completed.
   *
   * @return Stream
   */
  public Stream<RowSet> stream();

  /**
   * Return parallel stream of the number of data after the query is completed.
   *
   * @return Stream
   */
  public Stream<RowSet> parallelStream();
}
