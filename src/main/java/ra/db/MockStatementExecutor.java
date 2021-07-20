package ra.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import ra.db.connection.MockOnceConnection;
import ra.db.parameter.MysqlParameters;
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
  private List<String[]> fakeQueryData = new CopyOnWriteArrayList<String[]>();
  private Function<String, Integer> insertListener;
  private Function<String, Integer> tryExecuteListener;
  private Function<String, Integer> executeListener;
  private Function<List<String>, Integer> executeCommitListener;
  private Consumer<String> multiQueryListener;
  private Consumer<String> openListener;

  public MockStatementExecutor() {
    super(new MockOnceConnection(new MysqlParameters.Builder().build()));
  }

  public MockStatementExecutor(DatabaseConnection db) {
    super(db);
  }

  public void setInsertListener(Function<String, Integer> listener) {
    insertListener = listener;
  }

  public void setTryExecuteListener(Function<String, Integer> listener) {
    tryExecuteListener = listener;
  }

  public void setExecuteListener(Function<String, Integer> listener) {
    executeListener = listener;
  }

  public void setExecuteCommitListenerListener(Function<List<String>, Integer> listener) {
    executeCommitListener = listener;
  }

  public void setMultiOpenListener(Consumer<String> listener) {
    multiQueryListener = listener;
  }

  public void setOpenListener(Consumer<String> listener) {
    openListener = listener;
  }

  public void setIsLive(boolean isLive) {
    this.isLive = isLive;
  }

  @Override
  public boolean isLive() {
    return isLive;
  }

  @Override
  public void multiQuery(Consumer<MultiQuery> listener) {
    listener.accept(
        new MultiQuery(this::buildRecord, null) {

          @Override
          public RecordCursor executeQuery(String sql) {
            if (multiQueryListener != null) {
              multiQueryListener.accept(sql);
            }

            return buildRecord();
          }
        });
  }

  @Override
  public int execute(String sql) {
    if (executeListener != null) {
      return executeListener.apply(sql);
    }
    return 1;
  }

  @Override
  public int execute(String sql, Consumer<Exception> listener) {
    if (executeListener != null) {
      return executeListener.apply(sql);
    }
    return 1;
  }

  @Override
  public int executeCommit(List<String> sql, Consumer<Exception> listener) {
    if (executeCommitListener != null) {
      return executeCommitListener.apply(sql);
    }
    return 1;
  }

  @Override
  public int executeCommit(List<String> sql) {
    if (executeCommitListener != null) {
      return executeCommitListener.apply(sql);
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

    MockResultSet result = new MockResultSet(fakeQueryColumnsName);

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
  public void setFakeQueryColumnsNameList(List<String> columnsName) {
    if (columnsName != null) {
      fakeQueryColumnsName = columnsName.toArray(new String[columnsName.size()]);
    } else {
      columnsName = null;
    }
  }

  public void addFakeQuery(List<String> data) {
    fakeQueryData.add(data.toArray(new String[data.size()]));
  }

  public void addFakeQuery(String[] data) {
    fakeQueryData.add(data);
  }

  public void setFakeQueryColumnsName(String[] columnsName) {
    fakeQueryColumnsName = columnsName;
  }

  public void clearFakeQuery() {
    fakeQueryData.clear();
  }

  @Override
  public int tryExecute(String sql, Consumer<Exception> listener) {
    if (tryExecuteListener != null) {
      return tryExecuteListener.apply(sql);
    }
    return 1;
  }

  @Override
  public int insert(String sql, Consumer<Exception> errorListener) {
    if (insertListener != null) {
      return insertListener.apply(sql);
    }
    return 1;
  }
}
