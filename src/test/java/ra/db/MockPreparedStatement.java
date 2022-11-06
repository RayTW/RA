package ra.db;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock prepared statement.
 *
 * @author Ray Li
 */
public class MockPreparedStatement extends MockStatement implements PreparedStatement {
  private String sql = "";
  private Map<Integer, Object> parameterValues = new ConcurrentHashMap<>();

  public void setSql(String sql) {
    this.sql = sql;
  }

  public Object getparameterValues(int parameterIndex) {
    return parameterValues.get(parameterIndex);
  }

  public boolean containsIndex(int parameterIndex) {
    return parameterValues.containsKey(parameterIndex);
  }

  @Override
  public void addBatch() throws SQLException {}

  @Override
  public void clearParameters() throws SQLException {}

  @Override
  public boolean execute() throws SQLException {
    return super.execute(sql);
  }

  @Override
  public ResultSet executeQuery() throws SQLException {
    return super.executeQuery(sql);
  }

  @Override
  public int executeUpdate() throws SQLException {
    return super.executeUpdate(sql);
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return null;
  }

  @Override
  public ParameterMetaData getParameterMetaData() throws SQLException {
    return null;
  }

  @Override
  public void setArray(int parameterIndex, Array x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setBlob(int parameterIndex, Blob x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
    put(parameterIndex, inputStream);
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream, long length)
      throws SQLException {
    put(parameterIndex, inputStream);
  }

  @Override
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setByte(int parameterIndex, byte x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
    put(parameterIndex, reader);
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, int length)
      throws SQLException {
    put(parameterIndex, reader);
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, long length)
      throws SQLException {
    put(parameterIndex, reader);
  }

  @Override
  public void setClob(int parameterIndex, Clob x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    put(parameterIndex, reader);
  }

  @Override
  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
    put(parameterIndex, reader);
  }

  @Override
  public void setDate(int parameterIndex, Date x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setDouble(int parameterIndex, double x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setFloat(int parameterIndex, float x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setInt(int parameterIndex, int x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setLong(int parameterIndex, long x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
    put(parameterIndex, value);
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value, long length)
      throws SQLException {
    put(parameterIndex, value);
  }

  @Override
  public void setNClob(int parameterIndex, NClob value) throws SQLException {
    put(parameterIndex, value);
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    put(parameterIndex, reader);
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    put(parameterIndex, reader);
  }

  @Override
  public void setNString(int parameterIndex, String value) throws SQLException {
    put(parameterIndex, value);
  }

  @Override
  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    put(parameterIndex, sqlType);
  }

  @Override
  public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
    put(parameterIndex, sqlType);
  }

  @Override
  public void setObject(int parameterIndex, Object x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength)
      throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setRef(int parameterIndex, Ref x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setRowId(int parameterIndex, RowId x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    put(parameterIndex, xmlObject);
  }

  @Override
  public void setShort(int parameterIndex, short x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setString(int parameterIndex, String x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setTime(int parameterIndex, Time x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setURL(int parameterIndex, URL x) throws SQLException {
    put(parameterIndex, x);
  }

  @Override
  public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
    put(parameterIndex, x);
  }

  private void put(int parameterIndex, Object x) {
    parameterValues.put(parameterIndex, x == null ? Object.class : x);
  }
}
