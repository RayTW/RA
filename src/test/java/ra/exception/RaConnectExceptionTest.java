package ra.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test class.
 *
 * @author Ray Li
 */
public class RaConnectExceptionTest {

  @Test
  public void testCreateRaConnectException() {
    RaConnectException e = new RaConnectException();

    assertNull(e.getCause());
  }

  @Test
  public void testCreateRaConnectExceptionFromCause() {
    RaSqlException cause = new RaSqlException();
    RaConnectException e = new RaConnectException(cause);

    assertEquals(cause, e.getCause());
  }

  @Test
  public void testCreateRaConnectExceptionFromeMessage() {
    RaSqlException cause = new RaSqlException();
    RaConnectException e = new RaConnectException("test", cause);

    assertEquals(cause, e.getCause());
    assertEquals("ra.exception.RaSqlException test", e.getMessage());
  }

  @Test
  public void testCreateRaConnectExceptionFromeNullMessage() {
    RaSqlException cause = new RaSqlException();
    RaConnectException e = new RaConnectException(null, cause);

    assertEquals(cause, e.getCause());
    assertEquals("ra.exception.RaSqlException", e.getMessage());
  }
}
