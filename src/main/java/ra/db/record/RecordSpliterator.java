package ra.db.record;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import ra.db.Row;
import ra.db.RowSet;

/**
 * Parallel processing record.
 *
 * @author Ray Li
 */
public class RecordSpliterator<T extends Record> implements Spliterator<RowSet> {
  private final T record;
  private int origin;
  private final int fence;

  /**
   * initialize.
   *
   * @param record record
   * @param origin origin
   * @param fence fence
   */
  public RecordSpliterator(T record, int origin, int fence) {
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
        columnName = record.getColumnName(j);
        List<byte[]> v = record.getColumn(columnName);

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
      return new RecordSpliterator<T>(this.record, lo, mid);
    }

    return null;
  }
}
