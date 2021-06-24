package ra.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/** Test class. */
public class DoubleReferenceTest {

  @Test
  public void testSetGet() {
    DoubleReference ref = new DoubleReference();

    ref.set(Double.MAX_VALUE);

    assertEquals(Double.MAX_VALUE, ref.get(), 100);
  }

  @Test
  public void testToString() {
    DoubleReference ref = new DoubleReference(Double.MIN_VALUE);

    assertNotNull(ref.toString());
  }
}
