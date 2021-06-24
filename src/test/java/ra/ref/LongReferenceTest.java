package ra.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/** Test class. */
public class LongReferenceTest {

  @Test
  public void testSetGet() {
    LongReference ref = new LongReference();

    ref.set(Long.MAX_VALUE);

    assertEquals(Long.MAX_VALUE, ref.get());
  }

  @Test
  public void testToString() {
    LongReference ref = new LongReference(Long.MIN_VALUE);

    assertNotNull(ref.toString());
  }
}
