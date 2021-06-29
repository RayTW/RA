package ra.net.processor;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import ra.net.NetService;
import ra.ref.Reference;
import ra.server.basis.CommandsVerification;
import ra.util.Utility;

/** Test class. */
public class NetCommandProcessorTest {

  @Before
  public void setUp() throws Exception {
    CommandsVerification.loadCommands("./unittest/commands.json");
  }

  @Test
  public void testSucessHeartbeat() {
    String source = Utility.get().readFile("./unittest/CommandHeartbeat.json");
    NetService.NetRequest.Builder builder = new NetService.NetRequest.Builder();
    Reference<String> result = new Reference<>(source);

    builder.setText(source);

    NetCommandProcessor cmd =
        new NetCommandProcessor() {

          @Override
          public void commandProcess(NetService.NetRequest request) {
            result.set(request.getText());
          }
        };

    cmd.commandProcess(builder.build());

    assertEquals(source, result.get());
  }
}
