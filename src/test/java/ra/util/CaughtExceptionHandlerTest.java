package ra.util;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/** Test class. */
public class CaughtExceptionHandlerTest {

  @Test
  public void testGetThrowableDetail() {
    String stackTrace = CaughtExceptionHandler.get().getThrowableDetail(new Exception());

    assertThat(stackTrace, startsWith("java.lang.Exception"));
  }
}
