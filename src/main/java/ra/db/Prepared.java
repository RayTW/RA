package ra.db;

import java.util.HashMap;
import java.util.Map;

/**
 * Prepared.
 *
 * @author Ray Li
 */
public class Prepared {
  private String sql;
  private Map<Integer, ParameterValue> values;

  public static QueryBuilder newQueryBuilder(String sql) {
    return new QueryBuilder(sql);
  }

  public Map<Integer, ParameterValue> getValues() {
    return values;
  }

  public String getSql() {
    return sql;
  }

  /** Builder. */
  public static final class QueryBuilder {
    private String sql;
    private Map<Integer, ParameterValue> values;

    public QueryBuilder(String sql) {
      this.sql = sql;
    }

    private Map<Integer, ParameterValue> getValues() {
      if (values == null) {
        values = new HashMap<>();
      }

      return values;
    }

    public QueryBuilder set(int index, ParameterValue value) {
      getValues().put(index, value);
      return this;
    }

    /**
     * Build.
     *
     * @return Prepared
     */
    public Prepared build() {

      if (sql == null) {
        throw new NullPointerException("sql is required.");
      }

      Prepared obj = new Prepared();

      obj.sql = sql;
      obj.values = values;

      return obj;
    }
  }
}
