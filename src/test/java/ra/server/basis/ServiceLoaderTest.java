package ra.server.basis;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/** Test class. */
public class ServiceLoaderTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testLoadService() throws ClassNotFoundException, IOException {
    ServiceLoader<ServiceLite<Object>> obj = new ServiceLoader<>();

    obj.loadClasses("ra.server.basis.service");

    ServiceLite<Object> service = obj.getService("/fake/test");

    assertNotNull(service);
  }

  @Test
  public void testLoadServiceNotFound() throws ClassNotFoundException, IOException {
    exceptionRule.expectMessage("\"/aaa/bbb/ccc\" not found");

    ServiceLoader<ServiceLite<Object>> obj = new ServiceLoader<>();

    obj.loadClasses("com.chungyo.server.basis.service");

    obj.getService("/aaa/bbb/ccc");
  }

  @Test
  public void testLoadServiceNoExietsPackage() throws ClassNotFoundException, IOException {
    ServiceLoader<ServiceLite<Object>> obj = new ServiceLoader<>();

    obj.loadClasses("a.b.c.xxx");

    assertEquals("Services[]", obj.toString());
  }

  @Test
  public void testToString() throws ClassNotFoundException, IOException {
    ServiceLoader<ServiceLite<Object>> obj = new ServiceLoader<>();

    obj.loadClasses("ra.server.basis.service");

    assertThat(obj.toString(), startsWith("Services["));
  }

  @Test
  public void testLoadNoCacheService() throws ClassNotFoundException, IOException {
    ServiceLoader<ServiceLite<Object>> obj = new ServiceLoader<>();

    obj.loadClasses("ra.server.basis.service");

    ServiceLite<Object> service = obj.getService("/service/no-cache");

    assertNotNull(service);
  }
}
