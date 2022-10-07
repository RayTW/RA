package ra.db;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import org.junit.Test;
import ra.db.connection.MockOnceConnection;
import ra.db.connection.OnceConnection;
import ra.db.parameter.H2Parameters;
import ra.db.parameter.MysqlParameters;
import ra.db.record.LastInsertId;
import ra.db.record.RecordCursor;
import ra.exception.RaConnectException;
import ra.exception.RaSqlException;

/** Test class. */
public class TransactionTest {
  private static final MysqlParameters.Builder MYSQL_PARAM =
      new MysqlParameters.Builder().setHost("127.0.0.1").setPort(1234);
  private static final H2Parameters.Builder H2_PARAM =
      new H2Parameters.Builder()
          .setProperties("DATABASE_TO_UPPER", "false")
          .setProperties("MODE", "MYSQL")
          .inMemory()
          .setName("databaseName");
  private static final String CREATE_TABLE_SQL =
      "CREATE TABLE `test_table` ("
          + "  `id` bigint auto_increment,"
          + "  `columnsTest` varchar(100) DEFAULT NULL"
          + ");";
  private static final String DROP_TABLE_SQL = "DROP TABLE test_table";
  private static final Prepared INSERT_SQL_BUILDER =
      Prepared.newBuilder("INSERT INTO test_table SET columnsTest=?;").build();

  @Test
  public void testTransactionDisconnectThrowException() {
    MockOnceConnection connection = new MockOnceConnection(MYSQL_PARAM.build());
    JdbcExecutor mocExector = new JdbcExecutor(connection);

    connection.setIsLive(false);

    try {
      mocExector.executeTransaction(
          transaction -> {
            transaction.executeQuery("SELECT ... FROM table;");
            return true;
          });
    } catch (Exception e) {
      assertThat(e, instanceOf(RaConnectException.class));
    }
  }

  @Test
  public void testTransactionQuery() {
    MockOnceConnection connection = new MockOnceConnection(MYSQL_PARAM.build());
    JdbcExecutor mocExector = new JdbcExecutor(connection);

    mocExector.executeTransaction(
        transaction -> {
          RecordCursor record = transaction.executeQuery("SELECT ... FROM table;");

          assertEquals(0, record.getRecordCount());

          return true;
        });

    assertTrue(connection.getMockConnection().isCommit());
    assertFalse(connection.getMockConnection().isRollback());
  }

  @Test
  public void testTransactionQueryThrowException() {
    MockOnceConnection connection = new MockOnceConnection(MYSQL_PARAM.build());
    JdbcExecutor mocExector = new JdbcExecutor(connection);

    connection.getMockConnection().setThrowExceptionAnyExecute(new SQLException("unit test"));

    try {
      mocExector.executeTransaction(
          transaction -> {
            transaction.executeQuery("SELECT ... FROM table;");
            return true;
          });
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }

  @Test
  public void testTransactionCommitInsert() {
    MockOnceConnection connection = new MockOnceConnection(MYSQL_PARAM.build());
    JdbcExecutor mocExector = new JdbcExecutor(connection);

    mocExector.executeTransaction(
        transaction -> {
          transaction.executeUpdate("INSERT INTO ...");

          return true;
        });

    assertTrue(connection.getMockConnection().isCommit());
    assertFalse(connection.getMockConnection().isRollback());
  }

  @Test
  public void testTransactionCommitInsertThrowException() {
    MockOnceConnection connection = new MockOnceConnection(MYSQL_PARAM.build());
    JdbcExecutor mocExector = new JdbcExecutor(connection);

    connection.getMockConnection().setThrowExceptionAnyExecute(new SQLException("unit test"));

    try {
      mocExector.executeTransaction(
          transaction -> {
            transaction.executeUpdate("INSERT INTO ...");
            return true;
          });
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }

  @Test
  public void testTransactionRollbackInsert() {
    MockOnceConnection connection = new MockOnceConnection(MYSQL_PARAM.build());
    JdbcExecutor mocExector = new JdbcExecutor(connection);

    mocExector.executeTransaction(
        transaction -> {
          transaction.executeUpdate("INSERT INTO ...");

          return false;
        });

    assertFalse(connection.getMockConnection().isCommit());
    assertTrue(connection.getMockConnection().isRollback());
  }

  @Test
  public void testTransactionCommitInsertLastIdFromNull() {
    MockOnceConnection connection = new MockOnceConnection(MYSQL_PARAM.build());
    JdbcExecutor mocExector = new JdbcExecutor(connection);

    mocExector.executeTransaction(
        transaction -> {
          LastInsertId lastId = transaction.insert("INSERT INTO ...");

          assertTrue(lastId.isNull());
          return true;
        });

    assertTrue(connection.getMockConnection().isCommit());
    assertFalse(connection.getMockConnection().isRollback());
  }

  @Test
  public void testTransactionCommitInsertLastIdThrowException() {
    MockOnceConnection connection = new MockOnceConnection(MYSQL_PARAM.build());
    JdbcExecutor mocExector = new JdbcExecutor(connection);

    connection.getMockConnection().setThrowExceptionAnyExecute(new SQLException("unit test"));

    try {
      mocExector.executeTransaction(
          transaction -> {
            transaction.insert("INSERT INTO ...");
            return true;
          });
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }

  @Test
  public void testTransactionPrepareInsertRollback() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      executor.executeUpdate(CREATE_TABLE_SQL);

      executor.executeTransaction(
          transaction -> {
            transaction.prepareExecuteUpdate(
                INSERT_SQL_BUILDER
                    .toBuilder()
                    .set(1, ParameterValue.string("test string"))
                    .build());

            transaction.prepareExecuteUpdate(
                INSERT_SQL_BUILDER
                    .toBuilder()
                    .set(1, ParameterValue.string("test 2ssstttrr"))
                    .build());

            return false;
          });

      RecordCursor record = executor.executeQuery("SELECT * FROM test_table;");
      executor.executeUpdate(DROP_TABLE_SQL);

      assertEquals(0, record.getRecordCount());
    }
  }

  @Test
  public void testTransactionPrepareInsertCommit() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      executor.executeUpdate(CREATE_TABLE_SQL);

      executor.executeTransaction(
          transaction -> {
            transaction.prepareExecuteUpdate(
                INSERT_SQL_BUILDER
                    .toBuilder()
                    .set(1, ParameterValue.string("test string"))
                    .build());

            transaction.prepareExecuteUpdate(
                INSERT_SQL_BUILDER
                    .toBuilder()
                    .set(1, ParameterValue.string("test 2ssstttrr"))
                    .build());

            return true;
          });

      RecordCursor record = executor.executeQuery("SELECT * FROM test_table;");
      executor.executeUpdate(DROP_TABLE_SQL);

      assertEquals(2, record.getRecordCount());
    }
  }

  @Test
  public void testTransactionPrepareQuery() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      executor.executeUpdate(CREATE_TABLE_SQL);
      executor.executeUpdate("INSERT INTO test_table SET columnsTest='test string';");
      executor.executeUpdate("INSERT INTO test_table SET columnsTest='this is test';");

      executor.executeTransaction(
          transaction -> {
            RecordCursor record =
                transaction.prepareExecuteQuery(
                    Prepared.newBuilder(
                            "SELECT id,columnsTest FROM test_table WHERE columnsTest=?;")
                        .set(1, ParameterValue.string("this is test"))
                        .build());

            assertEquals(1, record.getRecordCount());
            assertEquals("this is test", record.field("columnsTest").toString());
            return true;
          });

      executor.executeUpdate(DROP_TABLE_SQL);
    }
  }

  @Test
  public void testTransactionPrepareInsertLastId() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      executor.executeUpdate(CREATE_TABLE_SQL);

      executor.executeTransaction(
          transaction -> {
            LastInsertId result1 =
                transaction.insert("INSERT INTO test_table SET columnsTest='test string';");
            LastInsertId result2 =
                transaction.insert("INSERT INTO test_table SET columnsTest='this is test';");

            assertEquals(1, result1.toInt());
            assertEquals(2, result2.toInt());
            return true;
          });

      executor.executeUpdate(DROP_TABLE_SQL);
    }
  }

  @Test
  public void testTransactionPrepareInsertThrowException() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      executor.executeUpdate(CREATE_TABLE_SQL);

      try {
        executor.executeTransaction(
            transaction -> {
              transaction.prepareExecuteUpdate(
                  INSERT_SQL_BUILDER
                      .toBuilder()
                      .set(0, ParameterValue.string("test string"))
                      .build());

              return true;
            });
      } catch (Exception e) {
        assertThat(e, instanceOf(RaSqlException.class));
      }
      executor.executeUpdate(DROP_TABLE_SQL);
    }
  }

  @Test
  public void testTransactionPrepareQueryThrowException() {
    try (OnceConnection connection = new OnceConnection(H2_PARAM.build())) {
      connection.connect();
      StatementExecutor executor = connection.createStatementExecutor();

      executor.executeUpdate(CREATE_TABLE_SQL);
      try {
        executor.executeTransaction(
            transaction -> {
              transaction.prepareExecuteQuery(
                  INSERT_SQL_BUILDER
                      .toBuilder()
                      .set(0, ParameterValue.string("test string"))
                      .build());

              return true;
            });
      } catch (Exception e) {
        assertThat(e, instanceOf(RaSqlException.class));
      }
      executor.executeUpdate(DROP_TABLE_SQL);
    }
  }
}
