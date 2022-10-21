package ra.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import ra.db.connection.MockOnceConnection;
import ra.db.parameter.MysqlParameters;
import ra.db.record.LastInsertId;
import ra.db.record.Record;
import ra.db.record.RecordCursor;

/**
 * Mock {@link StatementExecutor}.
 *
 * @author Ray Li
 */
public class MockStatementExecutor extends JdbcExecutor {
  private boolean isLive = true;

  private String[] fakeQueryColumnsName;
  private Integer[] fakeQueryColumnsType;
  private List<String[]> fakeQueryData = new CopyOnWriteArrayList<String[]>();
  private Function<String, String> insertListener;
  private Function<String, Integer> tryExecuteListener;
  private Function<String, Integer> executeListener;
  private Consumer<String> openListener;

  /** Initialize. */
  public MockStatementExecutor() {
    super(new MockOnceConnection(new MysqlParameters.Builder().build()));
  }

  /**
   * Initialize.
   *
   * @param db database connection
   */
  public MockStatementExecutor(DatabaseConnection db) {
    super(db);
  }

  /**
   * Register listener.
   *
   * @param listener listener
   */
  public void setInsertListener(Function<String, String> listener) {
    insertListener = listener;
  }

  /**
   * Register listener.
   *
   * @param listener listener
   */
  public void setTryExecuteListener(Function<String, Integer> listener) {
    tryExecuteListener = listener;
  }

  /**
   * Register listener.
   *
   * @param listener listener
   */
  public void setExecuteListener(Function<String, Integer> listener) {
    executeListener = listener;
  }

  /**
   * Register listener.
   *
   * @param listener listener
   */
  public void setOpenListener(Consumer<String> listener) {
    openListener = listener;
  }

  /**
   * Set mock database state whether available.
   *
   * @param isLive database state
   */
  public void setIsLive(boolean isLive) {
    this.isLive = isLive;
  }

  @Override
  public boolean isLive() {
    return isLive;
  }

  @Override
  public int executeUpdate(String sql) {
    if (executeListener != null) {
      return executeListener.apply(sql);
    }
    return 1;
  }

  @Override
  public RecordCursor executeQuery(String sql) {
    if (openListener != null) {
      openListener.accept(sql);
    }

    if (!isLive) {
      return buildRecord();
    }

    Objects.requireNonNull(
        fakeQueryColumnsName,
        "fakeQueryColumnsName == null,"
            + " must invoke setFakeQueryColumnsName(List<String> columnsName)");

    MockResultSet result =
        MockResultSet.newBuilder()
            .setColumnLabel(fakeQueryColumnsName)
            .setColumnType(fakeQueryColumnsType)
            .build();

    fakeQueryData
        .stream()
        .forEach(
            array -> {
              String key = null;

              for (int i = 0; i < fakeQueryColumnsName.length; i++) {
                key = fakeQueryColumnsName[i];
                result.addValue(key, array[i]);
              }
            });
    Record record = buildRecord();

    try {
      record.convert(result);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return record;
  }

  /**
   * Setting all column name into the fake table.
   *
   * @param columnsName The list contains all the column names in the fake table.
   */
  public void setColumnsNames(String... columnsName) {
    fakeQueryColumnsName = columnsName;
  }

  /**
   * Setting all column type into the fake table.
   *
   * @param columnsType The list contains all the column names in the fake table.
   */
  public void setColumnsTypes(Integer... columnsType) {
    fakeQueryColumnsType = columnsType;
  }

  /**
   * Add fake data.
   *
   * @param data single row data
   */
  public void addFakeQuery(List<String> data) {
    fakeQueryData.add(data.toArray(new String[data.size()]));
  }

  /**
   * Add fake data.
   *
   * @param data single row data
   */
  public void addFakeQuery(String[] data) {
    fakeQueryData.add(data);
  }

  /** Clear all fake data. */
  public void clearFakeQuery() {
    fakeQueryData.clear();
  }

  @Override
  public int tryExecuteUpdate(String sql) {
    if (tryExecuteListener != null) {
      return tryExecuteListener.apply(sql);
    }
    return 1;
  }

  @Override
  public LastInsertId insert(String sql) {
    String ret = "1";

    if (insertListener != null) {
      ret = insertListener.apply(sql);
    }
    return new LastInsertId(ret);
  }
}
