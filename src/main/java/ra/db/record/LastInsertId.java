package ra.db.record;

import com.mysql.cj.util.StringUtils;

/**
 * LAST_INSERT_ID.
 *
 * @author Ray Li
 */
public class LastInsertId {
  private String lastId;

  public LastInsertId(String id) {
    lastId = id;
  }

  public int toInt() {
    return Integer.parseInt(lastId);
  }

  public long toLong() {
    return Long.parseLong(lastId);
  }

  @Override
  public String toString() {
    return lastId;
  }

  public boolean isNull() {
    return StringUtils.isNullOrEmpty(lastId);
  }
}
