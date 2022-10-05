package ra.db;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import org.junit.Test;
import ra.db.connection.MockOnceConnection;
import ra.db.parameter.MysqlParameters;
import ra.db.record.LastInsertId;
import ra.db.record.RecordCursor;
import ra.exception.RaSqlException;

/** Test class. */
public class TransactionTest {
  private static final MysqlParameters.Builder MYSQL_PARAM =
      new MysqlParameters.Builder().setHost("127.0.0.1").setPort(1234);

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
  public void testTransactionCommitMultipleInsert() {
    MockOnceConnection connection = new MockOnceConnection(MYSQL_PARAM.build());
    JdbcExecutor mocExector = new JdbcExecutor(connection);

    mocExector.executeTransaction(
        transaction -> {
          transaction.executeUpdate(Arrays.asList("INSERT INTO ..."));

          return true;
        });

    assertTrue(connection.getMockConnection().isCommit());
    assertFalse(connection.getMockConnection().isRollback());
  }

  @Test
  public void testTransactionCommitMultipleInsertThrowException() {
    MockOnceConnection connection = new MockOnceConnection(MYSQL_PARAM.build());
    JdbcExecutor mocExector = new JdbcExecutor(connection);

    connection.getMockConnection().setThrowExceptionAnyExecute(new SQLException("unit test"));

    try {
      mocExector.executeTransaction(
          transaction -> {
            transaction.executeUpdate(Arrays.asList("INSERT INTO ..."));
            return true;
          });
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }

  @Test
  public void testTransactionRollbackMultipleInsert() {
    MockOnceConnection connection = new MockOnceConnection(MYSQL_PARAM.build());
    JdbcExecutor mocExector = new JdbcExecutor(connection);

    mocExector.executeTransaction(
        transaction -> {
          transaction.executeUpdate(Arrays.asList("INSERT INTO ..."));

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
          LastInsertId lastId = transaction.insertAndLastId("INSERT INTO ...");

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
            transaction.insertAndLastId("INSERT INTO ...");
            return true;
          });
    } catch (Exception e) {
      assertThat(e, instanceOf(RaSqlException.class));
    }
  }
}
