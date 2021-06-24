package ra.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/** Test class. */
public class UserTest {

  @Test
  public void testUserInitialize() {
    User obj = new User();

    obj.setIndex(0);
    obj.setIp("127.0.0.1");
    obj.setLangx("utf-8");
    obj.setName("name");

    assertEquals(0, obj.getIndex());
    assertEquals("127.0.0.1", obj.getIp());
    assertEquals("utf-8", obj.getLangx());
    assertEquals("name", obj.getName());
  }

  @Test
  public void testUserClose() {
    User obj = new User();

    obj.setIp("127.0.0.1");
    obj.setLangx("utf-8");
    obj.setName("name");

    obj.close();

    assertNull(obj.getIp());
    assertNull(obj.getLangx());
    assertNull(obj.getName());
  }
}
