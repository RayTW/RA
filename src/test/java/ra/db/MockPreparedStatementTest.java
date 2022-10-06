package ra.db;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import org.junit.Test;

/** Test class. */
public class MockPreparedStatementTest {

  @Test
  public void testAllMethod() throws SQLException {
    @SuppressWarnings("resource")
    MockPreparedStatement statement = new MockPreparedStatement();

    statement.setArray(1, null);
    statement.setAsciiStream(2, null);
    statement.setAsciiStream(3, null, 5);
    statement.setAsciiStream(4, null, 5L);
    statement.setBigDecimal(5, new BigDecimal("55.3366"));
    statement.setBinaryStream(6, null);
    statement.setBinaryStream(7, null, 5);
    statement.setBinaryStream(8, null, 5L);
    statement.setBlob(9, (Blob) null);
    statement.setBlob(10, null, 5);
    statement.setBlob(11, null, 5L);
    statement.setBoolean(12, true);
    statement.setByte(13, (byte) 0xa);
    statement.setBytes(14, new byte[] {0x1});
    statement.setCharacterStream(15, null);
    statement.setCharacterStream(16, null, 5);
    statement.setCharacterStream(17, null, 5L);
    statement.setClob(18, (Clob) null);
    statement.setClob(19, null, 5);
    statement.setClob(20, null, 5L);
    statement.setDouble(21, 0.1);
    statement.setInt(22, 1234);
    statement.setString(23, "string");
    statement.setLong(24, 464L);
    statement.setShort(25, (short) 122);
    statement.setFloat(26, 0.55f);

    assertTrue(statement.containsIndex(1));
    assertTrue(statement.containsIndex(2));
    assertTrue(statement.containsIndex(3));
    assertTrue(statement.containsIndex(4));
    assertTrue(statement.containsIndex(5));
    assertEquals("55.3366", statement.getparameterValues(5).toString());
    assertTrue(statement.containsIndex(6));

    assertTrue(statement.containsIndex(7));
    assertTrue(statement.containsIndex(8));
    assertTrue(statement.containsIndex(9));
    assertTrue(statement.containsIndex(10));
    assertTrue(statement.containsIndex(11));
    assertEquals(true, (boolean) statement.getparameterValues(12));
    assertEquals(0xa, (byte) statement.getparameterValues(13));
    assertArrayEquals(new byte[] {0x1}, (byte[]) statement.getparameterValues(14));
    assertTrue(statement.containsIndex(15));
    assertTrue(statement.containsIndex(16));
    assertTrue(statement.containsIndex(17));
    assertTrue(statement.containsIndex(18));
    assertTrue(statement.containsIndex(19));
    assertTrue(statement.containsIndex(20));
    assertEquals(0.1, (double) statement.getparameterValues(21), 1);
    assertEquals(1234, (int) statement.getparameterValues(22));
    assertEquals("string", statement.getparameterValues(23));
    assertEquals((short) 122, (short) statement.getparameterValues(25));
    assertEquals(0.55f, (float) statement.getparameterValues(26), 2);
  }
}
