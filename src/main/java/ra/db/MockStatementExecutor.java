package ra.db;

import java.io.UnsupportedEncodingException;
import java.util.AbstractList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Mock {@link StatementExecutor}.
 *
 * @author Ray Li
 */
public class MockStatementExecutor extends StatementExecutor {
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
    super(null);
  }

  public MockStatementExecutor(DatabaseConnection db) {
    super(null);
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
        new MultiQuery(null) {

          @Override
          public RecordSet executeQuery(String sql) {
            if (multiQueryListener != null) {
              multiQueryListener.accept(sql);
            }

            return new RecordSet();
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
      return new RecordSet();
    }

    Objects.requireNonNull(
        fakeQueryColumnsName,
        "mFakeQueryColumnsName == null,"
            + " must invoke setFakeQueryColumnsName(List<String> columnsName)");

    Map<String, AbstractList<byte[]>> map = new Hashtable<>();

    for (String key : fakeQueryColumnsName) {
      map.put(key, new Vector<>());
    }

    fakeQueryData
        .stream()
        .forEach(
            array -> {
              String key = null;
              AbstractList<byte[]> data = null;

              for (int i = 0; i < fakeQueryColumnsName.length; i++) {
                key = fakeQueryColumnsName[i];
                data = map.get(key);
                try {
                  data.add(array[i].getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
                }
              }
            });
    return RecordSet.newInstance(map);
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
