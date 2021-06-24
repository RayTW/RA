package ra.net;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ra.net.processor.NetServiceCommandProvider;

/** Test class. */
public class NetSocketWriterKeepTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testRegeditCode() {
    NetSocketWriterKeep obj =
        new NetSocketWriterKeep.Builder()
            .setHost("127.0.0.1")
            .setPort(9958)
            .setIndex(0)
            .setCommandProcessorProvider(new NetServiceCommandProvider())
            .build();

    obj.setRegeditCode("999");

    obj.close();

    assertEquals("999", obj.getRegeditCode());
    assertEquals(0, obj.getIndex());
  }

  @Test
  public void testHostIsNull() {
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("host == null or host.isEmpty()");

    new NetSocketWriterKeep.Builder()
        .setHost(null)
        .setCommandProcessorProvider(new NetServiceCommandProvider())
        .build();
  }

  @Test
  public void testHostIsEmpty() {
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("host == null or host.isEmpty()");

    new NetSocketWriterKeep.Builder()
        .setHost("")
        .setCommandProcessorProvider(new NetServiceCommandProvider())
        .build();
  }

  @Test
  public void testPortOutOfRange() {
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("The port number must be between 0 and 65535");

    new NetSocketWriterKeep.Builder()
        .setPort(-1)
        .setHost("127.0.0.1")
        .setCommandProcessorProvider(new NetServiceCommandProvider())
        .build();
  }
}
