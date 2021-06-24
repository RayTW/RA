package ra.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/** Test class. */
public class IntReferenceTest {

  @Test
  public void testSetGet() {
    IntReference ref = new IntReference();

    ref.set(Integer.MAX_VALUE);

    assertEquals(Integer.MAX_VALUE, ref.get());
  }

  @Test
  public void testToString() {
    IntReference ref = new IntReference(Integer.MIN_VALUE);

    assertNotNull(ref.toString());
  }
}
