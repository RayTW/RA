package ra.db;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import org.junit.Test;
import ra.db.connection.MockOriginalConnection;
import ra.db.connection.OnceConnection;
import ra.db.parameter.H2Parameters;
import ra.db.parameter.MysqlParameters;
import ra.db.record.RecordCursor;
import ra.exception.RaConnectException;
import ra.exception.RaSqlException;
import ra.util.Utility;

/** Test class. */
public class JdbcExecutorTest {
  private static final MysqlParameters.Builder MYSQL_PARAM =
      new MysqlParameters.Builder().setHost("127.0.0.1").setPort(1234);
  private static final H2Parameters.Builder H2_PARAM =
      new H2Parameters.Builder()
          .setProperties("DATABASE_TO_UPPER", "false")
          .setProperties("MODE", "MYSQL")
          .inMemory()
          .setName("databaseName");

  @Test
  public void testExecuteWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    try {
      executor.executeUpdate("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testTryExecuteWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    try {
      executor.tryExecuteUpdate("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testTryExecuteThrowException() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    connection
        .getMockConnection()
        .setThrowExceptionAnyExecute(new SQLException("testTryExecuteThrowException"));
    StatementExecutor executor = new JdbcExecutor(connection);

    try {
      executor.tryExecuteUpdate("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }

  @Test
  public void testTryExecuteWhenIsLiveTrue() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(true);

    try {
      executor.tryExecuteUpdate("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testInsertWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    try {
      executor.insert("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testInsertLastIdThrowException() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    StatementExecutor executor = new JdbcExecutor(connection);
    connection
        .getMockConnection()
        .setThrowExceptionAnyExecute(new SQLException("testInsertLastIdThrowException"));

    try {
      executor.insert("INSERT INTO 表格名 (欄位1, 欄位2, ...) VALUES (值1, 值2, ...);");
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }

  @Test
  public void testPrepareUpdateWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(MYSQL_PARAM.build());
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    try {
      executor.prepareExecuteUpdate(
          Prepared.newBuilder("UPDATE ... WHERE arg = ?;")
              .set(1, ParameterValue.string("args0"))
              .build());
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testPrepareQueryWhenIsLiveFalse() {
    MockOriginalConnection connection = new MockOriginalConnection(MYSQL_PARAM.build());
    StatementExecutor executor = new JdbcExecutor(connection);

    connection.setIsLive(false);

    try {
      executor.prepareExecuteQuery(
          Prepared.newBuilder("SELECT a,b,c FROM table WHERE name =?;")
              .set(1, ParameterValue.string("args0"))
              .build());
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testPrepareUpdateThrowException() {
    MockOriginalConnection connection = new MockOriginalConnection(MYSQL_PARAM.build());
    StatementExecutor executor = new JdbcExecutor(connection);

    connection
        .getMockConnection()
        .setThrowExceptionAnyExecute(new SQLException("testPrepareUpdateThrowException"));

    try {
      executor.prepareExecuteUpdate(
          Prepared.newBuilder("UPDATE ... WHERE arg = ?;")
              .set(1, ParameterValue.string("args0"))
              .build());
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }

  @Test
  public void testPrepareQueryThrowException() {
    MockOriginalConnection connection = new MockOriginalConnection(MYSQL_PARAM.build());
    StatementExecutor executor = new JdbcExecutor(connection);

    connection
        .getMockConnection()
        .setThrowExceptionAnyExecute(new SQLException("testPrepareQueryThrowException"));

    try {
      executor.prepareExecuteQuery(
          Prepared.newBuilder("SELECT a,b,c FROM table WHERE name =?;")
              .set(1, ParameterValue.string("args0"))
              .build());
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }

  @Test
  public void testExecuteFromInt() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `columnsTest` bigint"
              + ");";

      executor.executeUpdate(createTableSql);

      executor.prepareExecuteUpdate(
          Prepared.newBuilder("INSERT INTO test_table SET columnsTest=?;")
              .set(1, ParameterValue.int64(123))
              .build());

      RecordCursor record =
          executor.prepareExecuteQuery(
              Prepared.newBuilder("SELECT id,columnsTest FROM test_table WHERE id =?;")
                  .set(1, ParameterValue.int64(1))
                  .build());

      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertEquals(123, record.fieldInt("columnsTest"));
      assertEquals(123L, record.fieldLong("columnsTest"));
      assertEquals("123", record.field("columnsTest"));
      assertEquals(123.0, record.fieldDouble("columnsTest"), 1);
      assertEquals(123.0f, record.fieldFloat("columnsTest"), 1);
      assertEquals(new BigDecimal("123"), record.fieldBigDecimal("columnsTest"));
      assertEquals(123L, record.fieldObject("columnsTest"));
      assertThat(record.fieldObject("columnsTest"), instanceOf(Long.class));
    }
  }

  @Test
  public void testExecuteFromLong() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `columnsTest` int(10) UNSIGNED"
              + ");";

      executor.executeUpdate(createTableSql);

      executor.prepareExecuteUpdate(
          Prepared.newBuilder("INSERT INTO test_table SET columnsTest=?;")
              .set(1, ParameterValue.int64(8L))
              .build());

      RecordCursor record =
          executor.prepareExecuteQuery(
              Prepared.newBuilder("SELECT id,columnsTest FROM test_table WHERE columnsTest =?;")
                  .set(1, ParameterValue.int64(8L))
                  .build());

      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertEquals(8L, record.fieldLong("columnsTest"));
    }
  }

  @Test
  public void testExecuteFromFloat() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `columnsTest` double"
              + ");";

      executor.executeUpdate(createTableSql);

      executor.prepareExecuteUpdate(
          Prepared.newBuilder("INSERT INTO test_table SET columnsTest=?;")
              .set(1, ParameterValue.float64(3.334f))
              .build());

      RecordCursor record =
          executor.prepareExecuteQuery(
              Prepared.newBuilder("SELECT id,columnsTest FROM test_table WHERE columnsTest =?;")
                  .set(1, ParameterValue.float64(3.334f))
                  .build());

      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertEquals(3.334f, record.fieldFloat("columnsTest"), 3);
    }
  }

  @Test
  public void testExecuteFromDouble() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `columnsTest` double"
              + ");";

      executor.executeUpdate(createTableSql);

      executor.prepareExecuteUpdate(
          Prepared.newBuilder("INSERT INTO test_table SET columnsTest=?;")
              .set(1, ParameterValue.float64(3.334))
              .build());

      RecordCursor record =
          executor.prepareExecuteQuery(
              Prepared.newBuilder("SELECT id,columnsTest FROM test_table WHERE columnsTest =?;")
                  .set(1, ParameterValue.float64(3.334))
                  .build());

      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertEquals(3.334, record.fieldDouble("columnsTest"), 3);
    }
  }

  @Test
  public void testExecuteFromString() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `columnsTest` varchar(50)"
              + ");";

      executor.executeUpdate(createTableSql);

      executor.prepareExecuteUpdate(
          Prepared.newBuilder("INSERT INTO test_table SET columnsTest=?;")
              .set(1, ParameterValue.string("unitTestVarChar"))
              .build());

      RecordCursor record =
          executor.prepareExecuteQuery(
              Prepared.newBuilder("SELECT id,columnsTest FROM test_table WHERE columnsTest =?;")
                  .set(1, ParameterValue.string("unitTestVarChar"))
                  .build());

      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertEquals("unitTestVarChar", record.field("columnsTest"));

      try {
        record.fieldBytes("columnsTest");
      } catch (Exception e) {
        assertThat(e, instanceOf(RaSqlException.class));
      }
    }
  }

  @Test
  public void testExecuteFromStringToNumber() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `columnsTest` varchar(50)"
              + ");";

      executor.executeUpdate(createTableSql);
      executor.executeUpdate("INSERT INTO test_table SET columnsTest='120';");
      RecordCursor record =
          executor.executeQuery("SELECT id,columnsTest FROM test_table WHERE id =1;");
      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertEquals(120, record.fieldInt("columnsTest"));
      assertEquals(120L, record.fieldLong("columnsTest"));
      assertEquals(120.0f, record.fieldFloat("columnsTest"), 0);
      assertEquals(120.0, record.fieldDouble("columnsTest"), 0);
      assertEquals(new BigDecimal("120"), record.fieldBigDecimal("columnsTest"));
    }
  }

  @Test
  public void testExecuteFromByte() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `columnsTest` BINARY"
              + ");";

      executor.executeUpdate(createTableSql);

      executor.prepareExecuteUpdate(
          Prepared.newBuilder("INSERT INTO test_table SET columnsTest=?;")
              .set(1, ParameterValue.bytes(new byte[] {0xa}))
              .build());

      RecordCursor record =
          executor.prepareExecuteQuery(
              Prepared.newBuilder("SELECT id,columnsTest FROM test_table WHERE columnsTest =?;")
                  .set(1, ParameterValue.bytes(new byte[] {0xa}))
                  .build());

      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertEquals("0a", Utility.get().bytesToHex(record.fieldBytes("columnsTest")));
    }
  }

  @Test
  public void testExecuteFromBoolean() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `columnsTest` BOOLEAN"
              + ");";

      executor.executeUpdate(createTableSql);

      executor.prepareExecuteUpdate(
          Prepared.newBuilder("INSERT INTO test_table SET columnsTest=?;")
              .set(1, ParameterValue.bool(false))
              .build());

      RecordCursor record =
          executor.prepareExecuteQuery(
              Prepared.newBuilder("SELECT id,columnsTest FROM test_table WHERE columnsTest =?;")
                  .set(1, ParameterValue.bool(false))
                  .build());

      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertEquals("false", record.field("columnsTest"));
    }
  }

  @Test
  public void testExecuteFromBlob() throws SerialException, SQLException {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `columnsTest` BLOB(10K)"
              + ");";

      executor.executeUpdate(createTableSql);

      executor.prepareExecuteUpdate(
          Prepared.newBuilder("INSERT INTO test_table SET columnsTest=?;")
              .set(1, ParameterValue.blob(new SerialBlob("test".getBytes())))
              .build());

      RecordCursor record =
          executor.prepareExecuteQuery(
              Prepared.newBuilder("SELECT id,columnsTest FROM test_table WHERE columnsTest =?;")
                  .set(1, ParameterValue.blob(new SerialBlob("test".getBytes())))
                  .build());

      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertEquals("test", new String(record.fieldBytes("columnsTest")));
    }
  }

  @Test
  public void testExecuteFromDecimal() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `columnsTest` decimal(20,3) DEFAULT 0.000"
              + ");";

      executor.executeUpdate(createTableSql);

      executor.prepareExecuteUpdate(
          Prepared.newBuilder("INSERT INTO test_table SET columnsTest=?;")
              .set(1, ParameterValue.bigNumeric(new BigDecimal(12.334)))
              .build());

      RecordCursor record =
          executor.prepareExecuteQuery(
              Prepared.newBuilder("SELECT id,columnsTest FROM test_table WHERE columnsTest =?;")
                  .set(1, ParameterValue.bigNumeric(new BigDecimal("12.334")))
                  .build());

      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertEquals("12.334", record.fieldBigDecimal("columnsTest").toString());
    }
  }

  @Test
  public void testExecuteFromJson() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql = "CREATE TABLE `test_table` (`id` bigint ,  `columnsJson` JSON );";

      executor.executeUpdate(createTableSql);

      executor.prepareExecuteUpdate(
          Prepared.newBuilder("INSERT INTO test_table(id, columnsJson) VALUES (?, ? FORMAT JSON);")
              .set(1, ParameterValue.int64(1))
              .set(2, ParameterValue.string("{\"id\":10,\"name\":\"What''s this?\"}"))
              .build());

      RecordCursor record =
          executor.prepareExecuteQuery(
              Prepared.newBuilder("SELECT id,columnsJson FROM test_table WHERE id =?;")
                  .set(1, ParameterValue.int64(1))
                  .build());

      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertEquals(
          "{\"id\":10,\"name\":\"What''s this?\"}", new String(record.fieldBytes("columnsJson")));
    }
  }

  @Test
  public void testExecuteFromArray() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `columnsTest` VARCHAR(100) ARRAY DEFAULT NULL"
              + ");";

      executor.executeUpdate(createTableSql);

      executor.prepareExecuteUpdate(
          Prepared.newBuilder("INSERT INTO test_table SET columnsTest=?;")
              .set(1, ParameterValue.array("STRING", new Object[] {"array1", "array2"}))
              .build());

      RecordCursor record =
          executor.prepareExecuteQuery(
              Prepared.newBuilder("SELECT id,columnsTest FROM test_table WHERE columnsTest =?;")
                  .set(1, ParameterValue.array("STRING", new Object[] {"array1", "array2"}))
                  .build());

      executor.executeUpdate("DROP TABLE test_table");
      assertEquals(1, record.getRecordCount());
      assertArrayEquals(
          new Object[] {"array1", "array2"},
          record.fieldArray("columnsTest", Object[].class).toArray());
    }
  }

  @Test
  public void testExecuteUnsupportType() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `columnsTest` decimal(20,3) DEFAULT 0.000"
              + ");";

      executor.executeUpdate(createTableSql);

      executor.prepareExecuteUpdate(
          Prepared.newBuilder("INSERT INTO test_table SET columnsTest=?;")
              .set(1, ParameterValue.of(new Object(), Object.class))
              .build());
    } catch (Exception e) {
      assertThat(e, instanceOf(IllegalArgumentException.class));
    }
  }

  @Test
  public void testExecuteUpdateThrowException() {
    MockOriginalConnection connection = new MockOriginalConnection(null);
    connection
        .getMockConnection()
        .setThrowExceptionAnyExecute(new SQLException("testExecuteUpdateThrowException"));
    StatementExecutor executor = new JdbcExecutor(connection);

    try {
      executor.executeUpdate("UPDATE...;");
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }

  @Test
  public void testExecuteQueryThrowException() {
    MockOriginalConnection connection = new MockOriginalConnection(H2_PARAM.build());
    connection
        .getMockConnection()
        .setThrowExceptionAnyExecute(new SQLException("testExecuteQueryThrowException"));
    StatementExecutor executor = new JdbcExecutor(connection);

    try {
      executor.executeQuery("SELECT...;");
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }

  @Test
  public void testExecuteQueryFieldNull() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `name` varchar(100), "
              + "  `columnsValue` varchar(100) DEFAULT NULL"
              + ");";

      executor.executeUpdate(createTableSql);
      executor.executeUpdate("INSERT INTO test_table SET name='nameTest';");
      RecordCursor record =
          executor.executeQuery("SELECT id,name,columnsValue FROM test_table WHERE id =1;");
      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertNull(record.field("columnsValue"));
      assertNull(record.fieldBigDecimal("columnsValue"));
      assertNull(record.fieldObject("columnsValue"));
      assertNull(record.fieldBytes("columnsValue"));
      assertNull(record.field("columnsValue"));
    }
  }

  @Test
  public void testExecuteQueryFieldNullThrowException() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      String createTableSql =
          "CREATE TABLE `test_table` ("
              + "  `id` bigint auto_increment,"
              + "  `name` varchar(100), "
              + "  `columnsValue` varchar(100) DEFAULT NULL"
              + ");";

      executor.executeUpdate(createTableSql);
      executor.executeUpdate("INSERT INTO test_table SET name='nameTest';");
      RecordCursor record =
          executor.executeQuery("SELECT id,name,columnsValue FROM test_table WHERE id =1;");
      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertTrue(record.isNull("columnsValue"));

      try {
        record.fieldInt("columnsValue");
      } catch (Exception e) {
        assertThat(e, instanceOf(RaSqlException.class));
      }

      try {
        record.fieldLong("columnsValue");
      } catch (Exception e) {
        assertThat(e, instanceOf(RaSqlException.class));
      }

      try {
        record.fieldDouble("columnsValue");
      } catch (Exception e) {
        assertThat(e, instanceOf(RaSqlException.class));
      }

      try {
        record.fieldFloat("columnsValue");
      } catch (Exception e) {
        assertThat(e, instanceOf(RaSqlException.class));
      }
    }
  }
}
