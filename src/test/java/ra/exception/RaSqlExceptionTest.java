package ra.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import org.junit.Test;

/**
 * Test class.
 *
 * @author Ray Li
 */
public class RaSqlExceptionTest {

  @Test
  public void testCreateRaConnectException() {
    RaSqlException e = new RaSqlException();

    assertNull(e.getCause());
  }

  @Test
  public void testCreateRaConnectExceptionFromCause() {
    SQLException cause = new SQLException();
    RaSqlException e = new RaSqlException(cause);

    assertEquals(cause, e.getCause());
  }

  @Test
  public void testCreateRaConnectExceptionFromeCause() {
    SQLException cause = new SQLException();
    RaSqlException e = new RaSqlException("SELECT *88 FROM table;", cause);

    assertEquals(cause, e.getCause());

    assertEquals("java.sql.SQLException SELECT *88 FROM table;", e.getMessage());
  }

  @Test
  public void testCreateRaConnectExceptionFromeMessage() {
    RaSqlException e = new RaSqlException("test mesage");

    assertEquals("test mesage", e.getMessage());
  }

  @Test
  public void testCreateRaConnectExceptionFromeNullMessage() {
    RaSqlException e = new RaSqlException((String) null);

    assertNull(e.getMessage());
  }

  @Test
  public void testCreateRaConnectExceptionFromeNullMessageWithCause() {
    SQLException cause = new SQLException();
    RaSqlException e = new RaSqlException(null, cause);

    assertEquals(cause, e.getCause());
    assertEquals("java.sql.SQLException", e.getMessage());
  }
}
