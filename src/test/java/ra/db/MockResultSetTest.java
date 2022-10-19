package ra.db;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Types;
import org.junit.Test;

/** Test class. */
@SuppressWarnings("resource")
public class MockResultSetTest {

  @Test
  public void testGetColumnCount() throws SQLException, FileNotFoundException {
    MockResultSet resultSet =
        MockResultSet.newBuilder()
            .setColumnLabel("id", "name")
            .setColumnType(Types.BIGINT, Types.VARCHAR)
            .build();
    int actual = resultSet.getMetaData().getColumnCount();

    assertEquals(2, actual);
  }

  @Test
  public void testGetColumnLabel() throws SQLException, FileNotFoundException {
    MockResultSet resultSet =
        MockResultSet.newBuilder()
            .setColumnLabel("id", "name")
            .setColumnType(Types.BIGINT, Types.VARCHAR)
            .build();
    String actual = resultSet.getMetaData().getColumnLabel(2);

    assertEquals("name", actual);
  }

  @Test
  public void testGetBytesUseColumnIndex() throws SQLException, FileNotFoundException {
    MockResultSet resultSet =
        MockResultSet.newBuilder()
            .setColumnLabel("id", "name")
            .setColumnType(Types.BIGINT, Types.VARCHAR)
            .build();

    resultSet.addValue("id", 111);
    resultSet.addValue("name", "test");

    resultSet.addValue("id", 222);
    resultSet.addValue("name", "ccaa");
    resultSet.next();

    assertArrayEquals("test".getBytes(), resultSet.getBytes(2));
  }
}
