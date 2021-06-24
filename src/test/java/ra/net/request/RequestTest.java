package ra.net.request;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import ra.net.Sendable;

/** Test class. */
public class RequestTest {

  @Test
  public void testCreateEmptyRequest() {
    Request<String> request = new Request<>();

    assertEquals(0, request.getIndex());
  }

  @Test
  public void testCreateRequest() {

    Request<String> request = new Request<>(1);

    request.setDataBytes("abc".getBytes());
    request.setIp("1.2.3.4");
    request.setSender(
        new Sendable<String>() {

          @Override
          public void send(String message) {}

          @Override
          public void sendClose(String message) {}
        });

    assertArrayEquals("abc".getBytes(), request.getDataBytes());

    assertEquals(1, request.getIndex());
    assertEquals("1.2.3.4", request.getIp());
    assertNotNull(request.getSender());
  }

  @Test
  public void testCreateRequestUseClone() {
    Request<String> request = new Request<>(5);

    request.setDataBytes("abc".getBytes());
    request.setIp("1.2.3.4");
    request.setSender(
        new Sendable<String>() {

          @Override
          public void send(String message) {}

          @Override
          public void sendClose(String message) {}
        });

    Request<String> requestClone = new Request<>(request);

    assertEquals(request.getIndex(), requestClone.getIndex());
    assertArrayEquals(request.getDataBytes(), request.getDataBytes());
    assertEquals(request.getIp(), requestClone.getIp());
    assertEquals(request.getSender(), requestClone.getSender());
  }
}
