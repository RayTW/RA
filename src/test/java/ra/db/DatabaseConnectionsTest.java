package ra.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import ra.db.connection.MockOnceConnection;
import ra.db.parameter.MysqlParameters;

/** Test class. */
public class DatabaseConnectionsTest {

  @Test
  public void testNext() {
    Exception exception = null;
    StatementExecutor executor = null;
    DatabaseConnections dbConnection = generateDatabaseConnections(3);

    try {
      executor = dbConnection.next();
    } catch (Exception e) {
      exception = e;
    }
    assertNull(exception);
    assertNotNull(executor);
  }

  @Test
  public void testClose() {
    Exception exception = null;
    DatabaseConnections dbConnection = generateDatabaseConnections(3);

    try {
      dbConnection.close();
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }

    assertNull(exception);
  }

  @Test
  public void testGetDbConnection() {
    Exception exception = null;
    DatabaseConnection db = null;
    DatabaseConnections dbConnection = generateDatabaseConnections(3);

    try {
      db = dbConnection.getConnection(0);
    } catch (Exception e) {
      exception = e;
    }

    assertNull(exception);
    assertNotNull(db);
  }

  private DatabaseConnections generateDatabaseConnections(int connectCount) {
    MysqlParameters.Builder builder = new MysqlParameters.Builder();
    builder
        .setHost("127.0.0.1")
        .setName("dbName")
        .setPassword("112233")
        .setPort(3306)
        .setUser("user");
    MysqlParameters obj = builder.build();

    DatabaseConnections db = new DatabaseConnections();
    try {
      db.connect(obj, connectCount, () -> new MockOnceConnection(obj));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return db;
  }
}
