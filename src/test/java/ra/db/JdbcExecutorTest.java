package ra.db;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
          .setProperties("MODE", "MYSQL");

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
  public void testExecuteFromeInt() {
    try (OnceConnection connection =
        new OnceConnection(H2_PARAM.inMemory().setName("databaseName").build())) {
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
              .set(1, ParameterValue.int64(8))
              .build());

      RecordCursor record =
          executor.prepareExecuteQuery(
              Prepared.newBuilder("SELECT id,columnsTest FROM test_table WHERE columnsTest =?;")
                  .set(1, ParameterValue.int64(8))
                  .build());

      executor.executeUpdate("DROP TABLE test_table");

      assertEquals(1, record.getRecordCount());
      assertEquals(8, record.fieldInt("columnsTest"));
    }
  }

  @Test
  public void testExecuteFromeLong() {
    try (OnceConnection connection =
        new OnceConnection(H2_PARAM.inMemory().setName("databaseName").build())) {
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
  public void testExecuteFromeFloat() {
    try (OnceConnection connection =
        new OnceConnection(H2_PARAM.inMemory().setName("databaseName").build())) {
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
  public void testExecuteFromeDouble() {
    try (OnceConnection connection =
        new OnceConnection(H2_PARAM.inMemory().setName("databaseName").build())) {
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
  public void testExecuteFromeString() {
    try (OnceConnection connection =
        new OnceConnection(H2_PARAM.inMemory().setName("databaseName").build())) {
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
    }
  }

  @Test
  public void testExecuteFromeByte() {
    try (OnceConnection connection =
        new OnceConnection(H2_PARAM.inMemory().setName("databaseName").build())) {
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
  public void testExecuteFromeBoolean() {
    try (OnceConnection connection =
        new OnceConnection(H2_PARAM.inMemory().setName("databaseName").build())) {
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
  public void testExecuteFromeBlob() throws SerialException, SQLException {
    try (OnceConnection connection =
        new OnceConnection(H2_PARAM.inMemory().setName("databaseName").build())) {
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
  public void testExecuteFromeDecimal() {
    try (OnceConnection connection =
        new OnceConnection(H2_PARAM.inMemory().setName("databaseName").build())) {
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
  public void testExecuteUnsupportType() {
    try (OnceConnection connection =
        new OnceConnection(H2_PARAM.inMemory().setName("databaseName").build())) {
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
    MockOriginalConnection connection =
        new MockOriginalConnection(H2_PARAM.inMemory().setName("databaseName").build());
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
}
