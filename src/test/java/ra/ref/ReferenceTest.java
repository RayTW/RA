package ra.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Test class. */
public class ReferenceTest {

  @Test
  public void testSetGet() {
    Reference<String> ref = new Reference<>();

    ref.set("value");

    assertEquals("value", ref.get());
  }

  @Test
  public void testToString() {
    Reference<String> ref = new Reference<>("value");

    assertNotNull(ref.toString());
  }

  @Test
  public void testIsNullNoAssign() {
    Reference<String> ref = new Reference<>(null);

    assertTrue(ref.isNull());
  }

  @Test
  public void testNotNullAssign() {
    Reference<String> ref = new Reference<>("value");

    assertTrue(ref.isNotNull());
  }

  @Test
  public void testIsNullAssign() {
    Reference<String> ref = new Reference<>("value");

    assertFalse(ref.isNull());
  }

  @Test
  public void testNotNullNoAssign() {
    Reference<String> ref = new Reference<>(null);

    assertFalse(ref.isNotNull());
  }
}
