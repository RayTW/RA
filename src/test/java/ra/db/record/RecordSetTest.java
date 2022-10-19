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
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ra.db.DatabaseCategory;
import ra.db.MockResultSet;
import ra.db.MockStatement;
import ra.exception.RaSqlException;
import test.mock.resultset.MockArray;

/** Test class. */
public class RecordSetTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  private static final MockResultSet.Builder BUILDER_LASTID =
      MockResultSet.newBuilder().setColumnLabel("lastid").setColumnType(Types.VARCHAR);
  private static final MockResultSet.Builder BUILDER_ID_NAME =
      MockResultSet.newBuilder()
          .setColumnLabel("id", "name")
          .setColumnType(Types.INTEGER, Types.VARCHAR);

  @Test
  public void testCallNewColumnContainer() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL)) {
      assertNotNull(record.newColumnContainer());
    }
  }

  @Test
  public void testFieldNamesSetListenerUsingLastid() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = BUILDER_LASTID.build()) {

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
        MockResultSet result = BUILDER_LASTID.build()) {

      result.addValue("lastid", "100");
      record.convert(result);

      assertEquals("100", record.field(1));
    }
  }

  @Test
  public void testExecuteQueryAllColumnType() throws SQLException {
    MockResultSet result =
        MockResultSet.newBuilder()
            .setColumnLabel(
                new String[] {
                  "Blob",
                  "String",
                  "Short",
                  "Int",
                  "Long",
                  "Float",
                  "Double",
                  "DoubleDecima",
                  "BigDecimal"
                })
            .setColumnType(
                Types.BLOB,
                Types.VARCHAR,
                Types.TINYINT,
                Types.INTEGER,
                Types.BIGINT,
                Types.FLOAT,
                Types.DOUBLE,
                Types.NUMERIC,
                Types.NUMERIC)
            .build();
    result.addValue("Blob", "blobValue".getBytes());
    result.addValue("String", "StringValue");
    result.addValue("Short", Short.MAX_VALUE);
    result.addValue("Int", Integer.MAX_VALUE);
    result.addValue("Long", Long.MAX_VALUE);
    result.addValue("Float", Float.MAX_VALUE);
    result.addValue("Double", Double.MAX_VALUE);
    result.addValue("DoubleDecima", new BigDecimal("0.33333333333"));
    result.addValue("BigDecimal", new BigDecimal(String.valueOf(Math.PI)));

    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL)) {
      record.convert(result);

      record.first();
      assertArrayEquals("blobValue".getBytes(), record.fieldBytes("Blob"));
      assertEquals("StringValue", record.field("String"));
      assertEquals(Integer.MAX_VALUE, record.fieldInt("Int"));
      assertEquals(Long.MAX_VALUE, record.fieldLong("Long"));
      assertEquals(Float.MAX_VALUE, record.fieldFloat("Float"), 0);
      assertEquals(Double.MAX_VALUE, record.fieldDouble("Double"), 0);
      assertEquals(new BigDecimal(String.valueOf(Math.PI)), record.fieldBigDecimal("BigDecimal"));

      assertEquals("StringValue", record.fieldObject("String"));
      assertThat(record.fieldObject("Blob"), instanceOf(Object.class));
    }
  }

  @Test
  public void testFieldUsingCursorOperations() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = BUILDER_ID_NAME.build()) {

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

      assertFalse(record.isBof());
      assertEquals("2", record.field("id"));
      assertEquals("bbb", record.field("name"));

      record.previous();
      assertEquals("1", record.field("id"));
      assertEquals("aaa", record.field("name"));

      record.end();
      assertEquals("4", record.field("id"));
      assertTrue(record.isNull("name"));

      record.end();
      record.next();
      assertTrue(record.isEof());

      record.first();
      assertEquals("1", record.field("id"));
      assertEquals("aaa", record.field("name"));

      record.first();
      record.previous();

      assertTrue(record.isBof());
      assertFalse(record.isEof());

      record.move(2);
      assertEquals("2", record.field("id"));
      assertEquals("bbb", record.field("name"));
    }
  }

  @Test
  public void testGetRecordCount() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = BUILDER_ID_NAME.build()) {
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
      MockResultSet result =
          MockResultSet.newBuilder()
              .setColumnLabel("id", "name", "age")
              .setColumnType(Types.BIGINT, Types.VARCHAR, Types.INTEGER)
              .build();

      result.addValue("id", 1);
      result.addValue("name", "aaa");
      result.addValue("age", 5);

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
        MockResultSet result =
            MockResultSet.newBuilder()
                .setColumnLabel("id", "name", "age")
                .setColumnType(Types.BIGINT, Types.VARCHAR, Types.INTEGER)
                .build(); ) {

      result.addValue("id", 1);
      result.addValue("name", "aaa");
      result.addValue("age", 33);

      result.addValue("id", 2);
      result.addValue("name", "aaabb");
      result.addValue("age", 22);

      record.convert(result);

      String actual = record.toString();

      assertEquals(
          System.lineSeparator()
              + "|id|name |age|"
              + System.lineSeparator()
              + "|1 |aaa  |33 |"
              + System.lineSeparator()
              + "|2 |aaabb|22 |"
              + System.lineSeparator(),
          actual);
    }
  }

  @Test
  public void testFieldBytes() throws SQLException {
    byte[] expecteds = new byte[] {0xf, 0xf};

    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result =
            MockResultSet.newBuilder()
                .setColumnLabel("blobData")
                .setColumnType(Types.BLOB)
                .build()) {
      result.addValue("blobData", new byte[] {0xf, 0xf});

      record.convert(result);

      assertArrayEquals(expecteds, record.fieldBytes("blobData"));
    }
  }

  @Test
  public void testGetFieldIsNull() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = BUILDER_ID_NAME.build()) {
      result.addValue("id", 1);
      result.addValue("name", (String) null);
      result.addValue("id", 2);
      result.addValue("name", "bbb");

      record.convert(result);

      record.first();
      assertTrue(record.isNull("name"));
    }
  }

  @Test
  public void testGetFieldIsNullUseForeach() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.MYSQL);
        MockResultSet result = BUILDER_ID_NAME.build()) {

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
        MockResultSet result = BUILDER_ID_NAME.build()) {

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
        MockResultSet result = BUILDER_ID_NAME.build()) {

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
        MockResultSet result = BUILDER_ID_NAME.build()) {
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
      assertEquals("aaa", record.field("name"));

      record.next();

      assertEquals("bbb", record.field("name"));
      record.next();

      assertEquals("ccc", record.field("name"));

      record.next();
      assertEquals("ddd", record.field("name"));
    }
  }

  @Test
  public void testH2ResultFromInt() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.H2);
        MockResultSet result = BUILDER_ID_NAME.build()) {
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
        MockResultSet result = BUILDER_ID_NAME.build()) {
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
        MockResultSet result = BUILDER_ID_NAME.build()) {
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
        MockResultSet result = BUILDER_ID_NAME.build()) {
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
  public void testBigQueryResultFromFloat() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.BIGQUERY);
        MockResultSet result =
            MockResultSet.newBuilder()
                .setColumnLabel("id", "name")
                .setColumnType(Types.FLOAT, Types.VARCHAR)
                .build()) {
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
        MockResultSet result =
            MockResultSet.newBuilder()
                .setColumnLabel("id", "name")
                .setColumnType(Types.DOUBLE, Types.VARCHAR)
                .build()) {
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
        MockResultSet result =
            MockResultSet.newBuilder()
                .setColumnLabel("id", "name")
                .setColumnType(Types.DOUBLE, Types.VARCHAR)
                .build()) {
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
        MockResultSet result =
            MockResultSet.newBuilder()
                .setColumnLabel("id", "name")
                .setColumnType(Types.DOUBLE, Types.VARCHAR)
                .build()) {
      result.addValue("id", 3.14);
      result.addValue("name", "aaa");
      result.addValue("id", 3.22);
      result.addValue("name", "bbb");

      record.convert(result);

      assertEquals(2, record.getRecordCount());
      assertEquals(3.14, record.fieldBigDecimal("id").doubleValue(), 2);

      record.next();

      assertEquals(3.22, record.fieldBigDecimal("id").doubleValue(), 2);
    }
  }

  @Test
  public void testH2ResultIsNull() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.H2);
        MockResultSet result = BUILDER_ID_NAME.build()) {

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
        MockResultSet result = BUILDER_ID_NAME.build()) {

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
        MockResultSet result = BUILDER_ID_NAME.build()) {

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
      MockResultSet result = BUILDER_LASTID.build();

      result.addValue("lastid", "");
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
            MockResultSet result = BUILDER_LASTID.build();

            result.addValue("lastid", "876");
            return result;
          });

      assertEquals(876, record.getLastInsertId(st).toInt());
    }
  }

  @Test
  public void testSpannerResultFromBigDecimalDouble() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.SPANNER);
        MockResultSet result =
            MockResultSet.newBuilder()
                .setColumnLabel("id", "name")
                .setColumnType(Types.DOUBLE, Types.VARCHAR)
                .build()) {
      result.addValue("id", 3.14);
      result.addValue("name", "aaa");
      result.addValue("id", 3.22);
      result.addValue("name", "bbb");

      record.convert(result);

      assertEquals(2, record.getRecordCount());
      assertEquals(3.14, record.fieldBigDecimal("id").doubleValue(), 2);

      record.next();

      assertEquals(3.22, record.fieldBigDecimal("id").doubleValue(), 2);
    }
  }

  @Test
  public void testSpannerResultIsNull() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.SPANNER);
        MockResultSet result = BUILDER_ID_NAME.build()) {

      String name = null;

      result.addValue("id", 1);
      result.addValue("name", name);

      record.convert(result);

      assertEquals(1, record.getRecordCount());

      assertTrue(record.isNull("name"));
    }
  }

  @Test
  public void testSpannerColumnNameUseNullThrowException() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.SPANNER);
        MockResultSet result = BUILDER_ID_NAME.build()) {

      result.addValue("id", 1);
      result.addValue("name", "name");

      record.convert(result);
      record.fieldObject(null);
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }

  @Test
  public void testSpannerGetColumnName() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.SPANNER);
        MockResultSet result = BUILDER_ID_NAME.build()) {

      result.addValue("id", 1);
      result.addValue("name", "name");

      record.convert(result);

      assertEquals("id", record.getColumnName(1));
      assertEquals("name", record.getColumnName(2));
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
  public void testSpannerResultArray() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.SPANNER);
        MockResultSet result =
            new MockResultSet(Arrays.asList(Types.ARRAY), Arrays.asList("blobData"))) {

      result.addValue("blobData", new MockArray("aaa", "bbb"));
      result.addValue("blobData", new MockArray(111, 222));

      record.convert(result);

      List<Object> list = record.fieldArray("blobData", Object[].class);

      assertEquals(2, record.getRecordCount());
      assertArrayEquals(new String[] {"aaa", "bbb"}, list.toArray());
    }
  }

  @Test
  public void testSpannerResultArrayThrowException() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.SPANNER);
        MockResultSet result =
            new MockResultSet(Arrays.asList(Types.ARRAY), Arrays.asList("blobData"))) {

      result.addValue("blobData", new MockArray("aaa", "bbb"));
      result.addValue("blobData", new MockArray(111, 222));

      record.convert(result);

      record.fieldArray("blobData", Long[].class);
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }

  @Test
  public void testSpannerResultArrayUseNull() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.SPANNER);
        MockResultSet result =
            new MockResultSet(Arrays.asList(Types.ARRAY), Arrays.asList("blobData"))) {

      result.addValue("blobData", null);

      record.convert(result);

      List<Object> object = record.fieldArray("blobData", Object[].class);

      assertNull(object);
    }
  }

  @Test
  public void testSpannerResultString() throws SQLException {
    try (RecordSet record = new RecordSet(DatabaseCategory.SPANNER);
        MockResultSet result =
            new MockResultSet.Builder()
                .setColumnType(Types.VARCHAR)
                .setColumnLabel("stringColumn")
                .build()) {
      result.addValue("stringColumn", "xxxxgggraea");
      record.convert(result);

      assertEquals("xxxxgggraea", record.field("stringColumn"));
    }
  }
}
