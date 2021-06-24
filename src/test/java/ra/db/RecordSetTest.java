package ra.db;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ra.db.parameter.MysqlParameters;
import test.mock.MockDatabaseParameters;
import test.mock.resultset.MockAllColumnTypeResultSet;
import test.mock.resultset.MockCustomTableResultSet;
import test.mock.resultset.MockLastidResultSet;

/** Test class. */
public class RecordSetTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testCallNewColumnContainer() throws SQLException {
    try (RecordSet record = new RecordSet(new MockLastidResultSet())) {
      assertNotNull(record.newColumnContainer());
    }
  }

  @Test
  public void testFieldNamesSetListenerUsingLastid() throws SQLException {
    try (RecordSet record = new RecordSet(new MockLastidResultSet())) {

      record.fieldNames(
          fieldName -> {
            assertEquals("lastid", fieldName);
          });
    }
  }

  @Test
  public void testFieldNamesSetNullListenerUsingLastid() throws SQLException {
    exceptionRule.expect(NullPointerException.class);
    exceptionRule.expectMessage("consumer must not be null");

    try (RecordSet record = new RecordSet(new MockLastidResultSet())) {
      record.fieldNames(null);
    }
  }

  @Test
  public void testConvertUnknowParamThrowUnsupportedOperationException() throws SQLException {
    exceptionRule.expect(UnsupportedOperationException.class);
    exceptionRule.expectMessage("Unsupport DBCategory = null");

    try (RecordSet record = new RecordSet(new MockLastidResultSet())) {
      record.convert(new MockDatabaseParameters(), null);
    }
  }

  @Test
  public void testFieldUsingIndex() throws SQLException {
    try (RecordSet record = new RecordSet(new MockLastidResultSet(100))) {

      assertEquals("100", record.field(1));
    }
  }

  @Test
  public void testExecuteQueryAllColumnType() throws SQLException {
    MysqlParameters.Builder builder = new MysqlParameters.Builder();

    builder
        .setHost("127.0.0.1")
        .setName("dbName")
        .setPassword("112233")
        .setPort(3306)
        .setUser("user");

    MysqlParameters param = builder.build();
    MockStatement st = new MockStatement();

    st.setExecuteQueryListener((sql) -> new MockAllColumnTypeResultSet());

    try (RecordSet record = new RecordSet(new MockLastidResultSet())) {
      record.executeQuery(param, st, "select * from table");

      record.first();
      record.forEach(
          row -> {
            assertArrayEquals("blobValue".getBytes(), row.getBlob("Blob"));
            assertEquals("StringValue", row.getString("String"));
            assertEquals(Short.MAX_VALUE, row.getShort("Short"));
            assertEquals(Integer.MAX_VALUE, row.getInt("Int"));
            assertEquals(Long.MAX_VALUE, row.getLong("Long"));
            assertEquals(Float.MAX_VALUE, row.getFloat("Float"), 0);
            assertEquals(Double.MAX_VALUE, row.getDouble("Double"), 0);
            assertEquals(0.33333333333, row.getDoubleDecima("DoubleDecima"), 0);
            assertEquals(new BigDecimal(String.valueOf(Math.PI)), row.getBigDecimal("BigDecimal"));
          });
    }
  }

  @Test
  public void testFieldUsingCursorOperations() throws SQLException {
    try (RecordSet record =
        new RecordSet(
            new MockCustomTableResultSet(
                table -> {
                  // 新增第1筆資料
                  table.put("id", 1);
                  table.put("name", "aaa");

                  // 新增第2筆資料
                  table.put("id", 2);
                  table.put("name", "bbb");

                  // 新增第3筆資料
                  table.put("id", 3);
                  table.put("name", "ccc");

                  // 新增第4筆資料
                  String name = null;

                  table.put("id", 4);
                  table.put("name", name);
                },
                "id",
                "name"))) {
      record.next();

      assertEquals("2", record.field("id"));
      assertEquals("bbb", record.field("name"));

      record.previous();
      assertEquals("1", record.field("id"));
      assertEquals("aaa", record.field("name"));

      record.end();
      assertEquals("4", record.field("id"));
      assertEquals("", record.field("name"));

      record.end();
      record.next();
      assertTrue(record.isEof());

      record.first();
      assertEquals("1", record.field("id"));
      assertEquals("aaa", record.field("name"));

      record.first();
      record.previous();

      assertTrue(record.isBof());

      record.move(2);
      assertEquals("2", record.field("id"));
      assertEquals("bbb", record.field("name"));
    }
  }

  @Test
  public void testGetRecordCount() throws SQLException {
    try (RecordSet record =
        new RecordSet(
            new MockCustomTableResultSet(
                table -> {
                  // 新增第1筆資料
                  table.put("id", 1);
                  table.put("name", "aaa");

                  // 新增第2筆資料
                  table.put("id", 2);
                  table.put("name", "bbb");
                },
                "id",
                "name"))) {

      assertEquals(2, record.getRecordCount());
    }
  }

  @Test
  public void testGetFieldCount() throws SQLException {
    try (RecordSet record =
        new RecordSet(
            new MockCustomTableResultSet(
                table -> {
                  // 新增第1筆資料
                  table.put("id", 1);
                  table.put("name", "aaa");
                },
                "id",
                "name",
                "age"))) {

      // The columns are in order "null,id,name,age".
      assertEquals(4, record.getFieldCount());
    }
  }

  @Test
  public void testFieldUsingNonExistKey() {
    try (RecordSet record = new RecordSet()) {
      String actual = record.field("key");

      assertEquals("", actual);
    }
  }

  @Test
  public void testToString() throws SQLException {
    try (RecordSet record =
        new RecordSet(
            new MockCustomTableResultSet(
                table -> {
                  // 新增第1筆資料
                  table.put("id", 1);
                  table.put("name", "aaa");

                  // 新增第2筆資料
                  table.put("id", 2);
                  table.put("name", "aaabb");
                },
                "id",
                "name",
                "age"))) {

      String actual = record.toString();
      assertEquals(
          System.lineSeparator()
              + "|id|name |age|"
              + System.lineSeparator()
              + "|1 |aaa  |   |"
              + System.lineSeparator()
              + "|2 |aaabb|   |"
              + System.lineSeparator(),
          actual);
    }
  }

  @Test
  public void testFieldBytes() throws SQLException {
    byte[] expecteds = new byte[] {0xf, 0xf};

    try (RecordSet record =
        new RecordSet(
            new MockCustomTableResultSet(
                table -> {
                  // 新增第1筆資料
                  table.put("blobData", new byte[] {0xf, 0xf});
                },
                "blobData"))) {

      assertArrayEquals(expecteds, record.fieldBytes("blobData"));
    }
  }

  @Test
  public void testGetFieldIsNull() throws SQLException {
    try (RecordSet record =
        new RecordSet(
            new MockCustomTableResultSet(
                table -> {
                  // 新增第1筆資料
                  table.put("id", 1);
                  table.put("name", (String) null);

                  // 新增第2筆資料
                  table.put("id", 2);
                  table.put("name", "bbb");
                },
                "id",
                "name"))) {

      record.first();

      assertNull(record.optField("name"));
      assertNull(record.optField("name", 0));
    }
  }

  @Test
  public void testGetFieldIsNullUseForeach() throws SQLException {
    try (RecordSet record =
        new RecordSet(
            new MockCustomTableResultSet(
                table -> {
                  // 新增第1筆資料
                  table.put("id", 1);
                  table.put("name", (String) null);
                },
                "id",
                "name"))) {

      record.forEach(
          row -> {
            assertFalse(row.isNull("id"));
            assertTrue(row.isNull("name"));
          });
    }
  }

  @Test
  public void testStream() throws SQLException {
    try (RecordSet record =
        new RecordSet(
            new MockCustomTableResultSet(
                table -> {
                  table.put("id", 1);
                  table.put("name", "aaa");

                  table.put("id", 2);
                  table.put("name", "bbb");

                  table.put("id", 3);
                  table.put("name", "ccc");

                  table.put("id", 4);
                  table.put("name", "ddd");
                },
                "id",
                "name"))) {

      int actual =
          record
              .stream()
              .filter(row -> 2 == row.getInt("id"))
              .mapToInt(row -> row.getInt("id"))
              .findFirst()
              .getAsInt();

      assertEquals(2, actual);
    }
  }

  @Test
  public void testParallelStream() throws SQLException {
    try (RecordSet record =
        new RecordSet(
            new MockCustomTableResultSet(
                table -> {
                  table.put("id", 1);
                  table.put("name", "aaa");

                  table.put("id", 2);
                  table.put("name", "bbb");

                  table.put("id", 3);
                  table.put("name", "ccc");

                  table.put("id", 4);
                  table.put("name", "ddd");
                },
                "id",
                "name"))) {
      record
          .parallelStream()
          .forEach(
              r -> {
                System.out.println(r.getInt("id"));
              });

      int actual =
          record
              .parallelStream()
              .filter(row -> 3 == row.getInt("id"))
              .mapToInt(row -> row.getInt("id"))
              .findFirst()
              .getAsInt();

      assertEquals(3, actual);
    }
  }
}
