package ra.db.record;

import java.util.List;

/**
 * Record.
 *
 * @author Ray Li
 */
public interface Record extends RecordCursor, ResultConverter {
  /**
   * Returns name of column.
   *
   * @param index index
   * @return column name
   */
  public String getColumnName(int index);

  /**
   * Returns column.
   *
   * @param columnName columnName
   * @return column value
   */
  public List<byte[]> getColumn(String columnName);
}
