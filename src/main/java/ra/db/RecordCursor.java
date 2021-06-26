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
   * @param action consumer
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
   * @param name name of field
   * @return value
   */
  public String field(String name);

  /**
   * Use field name returns a value.
   *
   * @param name field name
   * @param charset charset, etc : utf8
   * @return value
   */
  public String field(String name, String charset);

  /**
   * Get the field value of the data pen of the specified field name. If the queried field is null,
   * return the feedback.
   *
   * @param name field name
   * @param lang charset, etc : utf8
   * @param feedback Return the feedback when if get value of the field is null.
   * @return value
   */
  public String field(String name, String lang, String feedback);

  /**
   * Get value of specific name.
   *
   * @param name field name
   * @param cursor record cursor
   * @return value
   */
  public String field(String name, int cursor);

  /**
   * Get value from the specific field name, and if field value is null will return null.
   *
   * @param name field name
   * @return value
   */
  public String optField(String name);

  /**
   * Get value from the specific field name, and if field value is null will return null.
   *
   * @param name field name
   * @param cursor record cursor
   * @return value
   */
  public String optField(String name, int cursor);

  /**
   * Get value from the specific field name, and if field value is null will return null.
   *
   * @param name field name
   * @param lang record cursor
   * @return value
   */
  public String optField(String name, String lang);

  /**
   * Get value from the specific field name, and if field value is null will return null.
   *
   * @param name field name
   * @param feedback Return the feedback when if get value of the field is null.
   * @return value
   */
  public String fieldFeedback(String name, String feedback);

  /**
   * Get value from the specific field name, and if field value is null will return null.
   *
   * @param name field name
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
   * @param action row of record
   */
  public void forEach(Consumer<RowSet> action);

  /**
   * Return the ordered stream of the number of data after the query is completed.
   *
   * @return Stream
   */
  public Stream<RowSet> stream();

  /**
   * Return the parallel stream of the number of data after the query is completed.
   *
   * @return Stream
   */
  public Stream<RowSet> parallelStream();
}
