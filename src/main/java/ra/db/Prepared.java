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

  public static Builder newBuilder(String sql) {
    return new Builder(sql);
  }

  public Map<Integer, ParameterValue> getValues() {
    return values;
  }

  public String getSql() {
    return sql;
  }

  /**
   * Clone a new builder with the current Prepared state.
   *
   * @return Builder
   */
  public Builder toBuilder() {
    Builder builder = new Builder(sql);

    if (values != null) {
      values.entrySet().forEach(e -> builder.set(e.getKey(), e.getValue()));
    }

    return builder;
  }

  /** Builder. */
  public static class Builder {
    private String sql;
    private Map<Integer, ParameterValue> values;

    public Builder(String sql) {
      this.sql = sql;
    }

    private Map<Integer, ParameterValue> getValues() {
      if (values == null) {
        values = new HashMap<>();
      }

      return values;
    }

    public Builder set(int index, ParameterValue value) {
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
