package test.mock.resultset;

import java.math.BigDecimal;
import ra.db.MockResultSet;

/** Test class. */
public class MockAllColumnTypeResultSet extends MockResultSet {
  private static String[] columnLabel =
      new String[] {
        "Blob", "String", "Short", "Int", "Long", "Float", "Double", "DoubleDecima", "BigDecimal"
      };

  /** Initialize. */
  public MockAllColumnTypeResultSet() {
    super(columnLabel);

    addValue("Blob", "blobValue".getBytes());
    addValue("String", "StringValue".getBytes());
    addValue("Short", String.valueOf(Short.MAX_VALUE).getBytes());
    addValue("Int", String.valueOf(Integer.MAX_VALUE).getBytes());
    addValue("Long", String.valueOf(Long.MAX_VALUE).getBytes());
    addValue("Float", String.valueOf(Float.MAX_VALUE).getBytes());
    addValue("Double", String.valueOf(Double.MAX_VALUE).getBytes());
    addValue("DoubleDecima", String.valueOf(new BigDecimal("0.33333333333")).getBytes());
    addValue("BigDecimal", String.valueOf(new BigDecimal(String.valueOf(Math.PI))).getBytes());
  }
}
