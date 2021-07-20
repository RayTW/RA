package ra.db.record;

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
import ra.db.DatabaseCategory;
import ra.db.MockResultSet;
import test.mock.resultset.MockAllColumnTypeResultSet;

/** Test class. */
public class RecordSetTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testCallNewColumnContainer() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL)) {
      assertNotNull(record.newColumnContainer());
    }
  }

  @Test
  public void testFieldNamesSetListenerUsingLastid() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = new MockResultSet("lastid")) {

      record.convert(result);

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

    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL)) {
      record.fieldNames(null);
    }
  }

  @Test
  public void testFieldUsingIndex() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = new MockResultSet("lastid")) {

      result.addValue("lastid", "100");
      record.convert(result);

      assertEquals("100", record.field(1));
    }
  }

  @Test
  public void testExecuteQueryAllColumnType() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL)) {
      record.convert(new MockAllColumnTypeResultSet());

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
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = new MockResultSet("id", "name")) {

      result.addValue("id", 1);
      result.addValue("name", "aaa");
      result.addValue("id", 2);
      result.addValue("name", "bbb");
      result.addValue("id", 3);
      result.addValue("name", "ccc");
      String name = null;
      result.addValue("id", 4);
      result.addValue("name", name);

      record.convert(result);

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
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = new MockResultSet("id", "name")) {
      result.addValue("id", 1);
      result.addValue("name", "aaa");

      result.addValue("id", 2);
      result.addValue("name", "bbb");

      record.convert(result);

      assertEquals(2, record.getRecordCount());
    }
  }

  @Test
  public void testGetFieldCount() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL)) {
      MockResultSet result = new MockResultSet("id", "name", "age");

      result.addValue("id", 1);
      result.addValue("name", "aaa");

      record.convert(result);

      // The columns are in order "null,id,name,age".
      assertEquals(4, record.getFieldCount());
    }
  }

  @Test
  public void testFieldUsingNonExistKey() {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL)) {
      String actual = record.field("key");

      assertEquals("", actual);
    }
  }

  @Test
  public void testToString() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = new MockResultSet("id", "name", "age")) {

      result.addValue("id", 1);
      result.addValue("name", "aaa");
      result.addValue("id", 2);
      result.addValue("name", "aaabb");

      record.convert(result);

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

    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = new MockResultSet("blobData")) {
      result.addValue("blobData", new byte[] {0xf, 0xf});

      record.convert(result);

      assertArrayEquals(expecteds, record.fieldBytes("blobData"));
    }
  }

  @Test
  public void testGetFieldIsNull() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = new MockResultSet("id", "name")) {
      result.addValue("id", 1);
      result.addValue("name", (String) null);
      result.addValue("id", 2);
      result.addValue("name", "bbb");

      record.convert(result);

      record.first();

      assertNull(record.optField("name"));
      assertNull(record.optField("name", 0));
    }
  }

  @Test
  public void testGetFieldIsNullUseForeach() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = new MockResultSet("id", "name")) {

      result.addValue("id", 1);
      result.addValue("name", (String) null);

      record.convert(result);

      record.forEach(
          row -> {
            assertFalse(row.isNull("id"));
            assertTrue(row.isNull("name"));
          });
    }
  }

  @Test
  public void testStream() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = new MockResultSet("id", "name")) {

      result.addValue("id", 1);
      result.addValue("name", "aaa");
      result.addValue("id", 2);
      result.addValue("name", "bbb");
      result.addValue("id", 3);
      result.addValue("name", "ccc");
      result.addValue("id", 4);
      result.addValue("name", "ddd");

      record.convert(result);

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
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = new MockResultSet("id", "name")) {

      result.addValue("id", 1);
      result.addValue("name", "aaa");
      result.addValue("id", 2);
      result.addValue("name", "bbb");
      result.addValue("id", 3);
      result.addValue("name", "ccc");
      result.addValue("id", 4);
      result.addValue("name", "ddd");

      record.convert(result);
      record.parallelStream().forEach(r -> System.out.println(r.getInt("id")));

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
