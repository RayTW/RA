package ra.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/** Test class. */
public class DefaultUserTest {

  @Test
  public void testUserInitialize() {
    DefaultUser obj = new DefaultUser();

    obj.setIndex(0);
    obj.setIp("127.0.0.1");
    obj.setName("name");

    assertEquals(0, obj.getIndex());
    assertEquals("127.0.0.1", obj.getIp());
    assertEquals("name", obj.getName());
  }

  @Test
  public void testUserClose() {
    DefaultUser obj = new DefaultUser();

    obj.setIp("127.0.0.1");
    obj.setName("name");

    obj.close();

    assertNull(obj.getIp());
    assertNull(obj.getName());
  }
}
