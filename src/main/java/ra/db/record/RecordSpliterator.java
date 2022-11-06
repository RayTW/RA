package ra.db.record;

import java.util.Spliterator;
import java.util.function.Consumer;

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
      Row row = new Row(record);

      synchronized (record) {
        record.move(origin + 1);
        action.accept(row);
      }
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
