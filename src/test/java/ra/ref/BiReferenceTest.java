package ra.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Test class. */
public class BiReferenceTest {

  @Test
  public void testSetGetLeft() {
    BiReference<String, Object> ref = new BiReference<>();

    ref.setLeft("left");

    assertEquals("left", ref.getLeft());
  }

  @Test
  public void testSetGetRight() {
    BiReference<String, Object> ref = new BiReference<>();
    Object expected = new Object();
    ref.setRight(expected);

    assertEquals(expected, ref.getRight());
  }

  @Test
  public void testToString() {
    BiReference<String, Object> ref = new BiReference<>("key", new Object());

    assertNotNull(ref.toString());
  }

  @Test
  public void testLeftRightIsNullNoAssign() {
    BiReference<String, Object> ref = new BiReference<>(null, null);

    assertTrue(ref.isLeftNull());
    assertTrue(ref.isRightNull());
  }

  @Test
  public void testLeftRightIsNullAssign() {
    BiReference<String, Object> ref = new BiReference<>("key", new Object());

    assertFalse(ref.isLeftNull());
    assertFalse(ref.isRightNull());
  }

  @Test
  public void testLeftRightNotNullAssign() {
    BiReference<String, Object> ref = new BiReference<>("key", new Object());

    assertTrue(ref.isNotLeftNull());
    assertTrue(ref.isNotRightNull());
  }

  @Test
  public void testLeftRightNotNullNoAssign() {
    BiReference<String, Object> ref = new BiReference<>(null, null);

    assertFalse(ref.isNotLeftNull());
    assertFalse(ref.isNotRightNull());
  }
}
