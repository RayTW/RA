package ra.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/** Test class. */
public class ShortReferenceTest {

  @Test
  public void testSetGet() {
    ShortReference ref = new ShortReference();

    ref.set(Short.MAX_VALUE);

    assertEquals(Short.MAX_VALUE, ref.get());
  }

  @Test
  public void testToString() {
    ShortReference ref = new ShortReference(Short.MIN_VALUE);

    assertNotNull(ref.toString());
  }
}
