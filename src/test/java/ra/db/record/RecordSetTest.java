package ra.db.record;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Types;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ra.db.DatabaseCategory;
import ra.db.MockResultSet;
import ra.db.MockStatement;
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
            assertEquals(0.33333333333, row.getBigDecimalDouble("DoubleDecima"), 0);
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
      assertNull(record.field("name"));

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

      assertNull(record.field("key"));
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
      assertNull(record.field("name"));
      assertNull(record.field("name", 0));
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

  @Test
  public void testBigQueryConvert() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.BIGQUERY);
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

      assertEquals(4, record.getRecordCount());
      assertEquals("aaa", record.field("name", 0));
      assertEquals("bbb", record.field("name", 1));
      assertEquals("ccc", record.field("name", 2));
      assertEquals("ddd", record.field("name", 3));
    }
  }

  @Test
  public void testH2ResultFromInt() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.H2);
        MockResultSet result = new MockResultSet("id", "name")) {
      result.addValue("id", 1);
      result.addValue("name", "aaa");
      result.addValue("id", 2);
      result.addValue("name", "bbb");

      record.convert(result);

      assertEquals(2, record.getRecordCount());
      assertEquals(1, record.fieldInt("id"));

      record.next();

      assertEquals(2, record.fieldInt("id"));
    }
  }

  @Test
  public void testBigQueryResultFromInt() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.BIGQUERY);
        MockResultSet result = new MockResultSet("id", "name")) {
      result.addValue("id", 1);
      result.addValue("name", "aaa");
      result.addValue("id", 2);
      result.addValue("name", "bbb");

      record.convert(result);

      assertEquals(2, record.getRecordCount());
      assertEquals(1, record.fieldInt("id"));

      record.next();

      assertEquals(2, record.fieldInt("id"));
    }
  }

  @Test
  public void testBigQueryResultFromLong() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.BIGQUERY);
        MockResultSet result = new MockResultSet("id", "name")) {
      result.addValue("id", 1L);
      result.addValue("name", "aaa");
      result.addValue("id", 2L);
      result.addValue("name", "bbb");

      record.convert(result);

      assertEquals(2, record.getRecordCount());
      assertEquals(1L, record.fieldLong("id"));

      record.next();

      assertEquals(2L, record.fieldLong("id"));
    }
  }

  @Test
  public void testBigQueryResultFromShort() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.BIGQUERY);
        MockResultSet result = new MockResultSet("id", "name")) {
      result.addValue("id", 1);
      result.addValue("name", "aaa");
      result.addValue("id", 2);
      result.addValue("name", "bbb");

      record.convert(result);

      assertEquals(2, record.getRecordCount());
      assertEquals(1, record.fieldShort("id"));

      record.next();

      assertEquals(2, record.fieldShort("id"));
    }
  }

  @Test
  public void testBigQueryResultFromFloat() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.BIGQUERY);
        MockResultSet result = new MockResultSet("id", "name")) {
      result.addValue("id", 3.14f);
      result.addValue("name", "aaa");
      result.addValue("id", 3.22f);
      result.addValue("name", "bbb");

      record.convert(result);

      assertEquals(2, record.getRecordCount());
      assertEquals(3.14f, record.fieldFloat("id"), 2);

      record.next();

      assertEquals(3.22f, record.fieldFloat("id"), 2);
    }
  }

  @Test
  public void testBigQueryResultFromDouble() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.BIGQUERY);
        MockResultSet result = new MockResultSet("id", "name")) {
      result.addValue("id", 3.14d);
      result.addValue("name", "aaa");
      result.addValue("id", 3.22d);
      result.addValue("name", "bbb");

      record.convert(result);

      assertEquals(2, record.getRecordCount());
      assertEquals(3.14d, record.fieldFloat("id"), 2);

      record.next();

      assertEquals(3.22d, record.fieldFloat("id"), 2);
    }
  }

  @Test
  public void testBigQueryResultFromBigDecimal() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.BIGQUERY);
        MockResultSet result = new MockResultSet("id", "name")) {
      result.addValue("id", 3.14);
      result.addValue("name", "aaa");
      result.addValue("id", 3.22);
      result.addValue("name", "bbb");

      record.convert(result);

      assertEquals(2, record.getRecordCount());
      assertEquals(new BigDecimal("3.14"), record.fieldBigDecimal("id"));

      record.next();

      assertEquals(new BigDecimal("3.22"), record.fieldBigDecimal("id"));
    }
  }

  @Test
  public void testBigQueryResultFromBigDecimalDouble() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.BIGQUERY);
        MockResultSet result = new MockResultSet("id", "name")) {
      result.addValue("id", 3.14);
      result.addValue("name", "aaa");
      result.addValue("id", 3.22);
      result.addValue("name", "bbb");

      record.convert(result);

      assertEquals(2, record.getRecordCount());
      assertEquals(3.14, record.fieldBigDecimalDouble("id"), 2);

      record.next();

      assertEquals(3.22, record.fieldBigDecimalDouble("id"), 2);
    }
  }

  @Test
  public void testH2ResultIsNull() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.H2);
        MockResultSet result = new MockResultSet("id", "name")) {

      String name = null;

      result.addValue("id", 1);
      result.addValue("name", name);

      record.convert(result);

      assertEquals(1, record.getRecordCount());

      assertTrue(record.isNull("name"));
    }
  }

  @Test
  public void testMySqlResultIsNull() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = new MockResultSet("id", "name")) {

      String name = null;

      result.addValue("id", 1);
      result.addValue("name", name);

      record.convert(result);

      assertEquals(1, record.getRecordCount());

      assertTrue(record.isNull("name"));
    }
  }

  @Test
  public void testBigQueryResultIsNull() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.BIGQUERY);
        MockResultSet result = new MockResultSet("id", "name")) {

      String name = null;

      result.addValue("id", 1);
      result.addValue("name", name);

      record.convert(result);

      assertEquals(1, record.getRecordCount());

      assertTrue(record.isNull("name"));
    }
  }

  @Test
  public void testBigQueryResultLastInserId() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.BIGQUERY)) {

      assertEquals(-1, record.getLastInsertId(new MockStatement()));
    } catch (Exception e) {
      assertThat(e, instanceOf(UnsupportedOperationException.class));
    }
  }

  @Test
  public void testH2ResultLastInserId() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.H2)) {

      MockStatement st = new MockStatement();
      MockResultSet result =
          new MockResultSet(Arrays.asList(Types.VARCHAR), Arrays.asList("h2lastId"));

      result.addValue("h2lastId", "2");
      st.setGeneratedKeys(result);

      assertEquals(2, record.getLastInsertId(st).toInt());
    }
  }

  @Test
  public void testH2ResultLastInserIdIsEmpty() throws SQLException {
    exceptionRule.expect(SQLWarning.class);
    exceptionRule.expectMessage("Failed to get last insert ID.");

    try (RecordSet record = new RecordSet(DatabaseCategory.H2)) {

      MockStatement st = new MockStatement();
      MockResultSet result = new MockResultSet("h2lastId");

      result.addValue("h2lastId", "");
      st.setGeneratedKeys(result);

      record.getLastInsertId(st).toInt();
    }
  }

  @Test
  public void testMySqlResultLastInserId() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL)) {

      MockStatement st = new MockStatement();

      st.setExecuteQueryListener(
          sql -> {
            MockResultSet result = new MockResultSet("lastid");

            result.addValue("lastid", "876");
            return result;
          });

      assertEquals(876, record.getLastInsertId(st).toInt());
    }
  }

  @Test
  public void testSpannerResultFromBigDecimalDouble() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.SPANNER);
        MockResultSet result = new MockResultSet("id", "name")) {
      result.addValue("id", 3.14);
      result.addValue("name", "aaa");
      result.addValue("id", 3.22);
      result.addValue("name", "bbb");

      record.convert(result);

      assertEquals(2, record.getRecordCount());
      assertEquals(3.14, record.fieldBigDecimalDouble("id"), 2);

      record.next();

      assertEquals(3.22, record.fieldBigDecimalDouble("id"), 2);
    }
  }

  @Test
  public void testSpannerResultIsNull() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.SPANNER);
        MockResultSet result = new MockResultSet("id", "name")) {

      String name = null;

      result.addValue("id", 1);
      result.addValue("name", name);

      record.convert(result);

      assertEquals(1, record.getRecordCount());

      assertTrue(record.isNull("name"));
    }
  }

  @Test
  public void testSpannerResultBlob() throws SQLException {
    byte[] expecteds = new byte[] {0xf, 0xf};

    try (RecordSet record = new RecordSet(DatabaseCategory.SPANNER);
        MockResultSet result =
            new MockResultSet(Arrays.asList(Types.BLOB), Arrays.asList("blobData"))) {
      result.addValue("blobData", new byte[] {0xf, 0xf});

      record.convert(result);

      assertArrayEquals(expecteds, record.fieldBytes("blobData"));
    }
  }

  @Test
  public void testSpannerResultString() throws SQLException {

    try (RecordSet record = new RecordSet(DatabaseCategory.SPANNER);
        MockResultSet result =
            new MockResultSet(Arrays.asList(Types.VARCHAR), Arrays.asList("stringColumn"))) {
      result.addValue("stringColumn", "xxxxgggraea");

      record.convert(result);

      assertEquals("xxxxgggraea", record.field("stringColumn"));
    }
  }
}
