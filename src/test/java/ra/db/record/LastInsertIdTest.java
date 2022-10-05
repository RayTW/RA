package ra.db.record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Test class. */
public class LastInsertIdTest {

  @Test
  public void testIsNull() {
    LastInsertId obj = new LastInsertId(null);

    assertNull(obj.toString());
    assertTrue(obj.isNull());
  }

  @Test
  public void testToInt() {
    LastInsertId obj = new LastInsertId("123");

    assertFalse(obj.isNull());
    assertEquals(123, obj.toInt());
  }

  @Test
  public void testToLong() {
    LastInsertId obj = new LastInsertId("123");

    assertFalse(obj.isNull());
    assertEquals(123L, obj.toLong());
  }

  @Test
  public void testToString() {
    LastInsertId obj = new LastInsertId("123");

    assertFalse(obj.isNull());
    assertEquals("123", obj.toString());
  }
}
