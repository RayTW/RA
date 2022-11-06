package test.mock.resultset;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Map;

/**
 * Mock.
 *
 * @author Ray Li
 */
public class MockArray implements Array {
  private Object[] values;

  public MockArray(Object... values) {
    this.values = values;
  }

  public MockArray() {}

  private void checkValues() throws SQLException {
    if (values == null) {
      throw new SQLException("Already freed or invalid"); // $NON-NLS-1$
    }
  }

  @Override
  public void free() throws SQLException {
    this.values = null;
  }

  @Override
  public Object getArray() throws SQLException {
    checkValues();
    return values;
  }

  @Override
  public Object getArray(Map<String, Class<?>> map) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public Object getArray(long index, int count) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public int getBaseType() throws SQLException {
    checkValues();
    return Types.OTHER;
  }

  @Override
  public String getBaseTypeName() throws SQLException {
    return Object[].class.getSimpleName();
  }

  @Override
  public ResultSet getResultSet() throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public ResultSet getResultSet(long index, int count) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map)
      throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public String toString() {
    return Arrays.toString(this.values);
  }
}
