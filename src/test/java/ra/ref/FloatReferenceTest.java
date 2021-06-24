package ra.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/** Test class. */
public class FloatReferenceTest {

  @Test
  public void testSetGet() {
    FloatReference ref = new FloatReference();

    ref.set(Float.MAX_VALUE);

    assertEquals(Float.MAX_VALUE, ref.get(), 100);
  }

  @Test
  public void testToString() {
    FloatReference ref = new FloatReference(Float.MIN_VALUE);

    assertNotNull(ref.toString());
  }
}
