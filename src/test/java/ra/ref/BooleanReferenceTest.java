package ra.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/** Test class. */
public class BooleanReferenceTest {

  @Test
  public void testSetGet() {
    BooleanReference ref = new BooleanReference();

    ref.set(true);

    assertEquals(true, ref.get());
  }

  @Test
  public void testToString() {
    BooleanReference ref = new BooleanReference(false);

    assertNotNull(ref.toString());
  }
}
