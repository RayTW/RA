package ra.net.processor;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import ra.net.request.DefaultRequest;
import ra.net.request.Request;
import ra.ref.Reference;
import ra.server.basis.CommandsVerification;
import ra.util.Utility;

/** Test class. */
public class CommandProcessorJsonTest {

  @Before
  public void setUp() throws Exception {
    CommandsVerification.loadCommands("./unittest/commands.json");
  }

  @Test
  public void testSucessHeartbeat() {
    String source = Utility.get().readFile("./unittest/CommandHeartbeat.json");
    Request<String> request = new Request<String>(0);
    Reference<String> result = new Reference<>(source);

    request.setDataBytes(source.getBytes());

    CommandProcessorJson cmd =
        new CommandProcessorJson() {

          @Override
          public void commandHandle(DefaultRequest request) {
            result.set(request.getSource());
          }
        };

    cmd.commandProcess(new DefaultRequest(request));

    assertEquals(source, result.get());
  }
}
