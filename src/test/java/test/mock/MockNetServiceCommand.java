package test.mock;

import ra.net.processor.CommandProcessorListener;
import ra.net.processor.CommandProcessorText;
import ra.net.processor.NetCommandProvider;
import ra.net.request.TextRequest;

/**
 * Mock class.
 *
 * @author Ray Li
 */
public class MockNetServiceCommand extends NetCommandProvider {
  @Override
  public CommandProcessorListener<String> createCommand() {
    return new CommandProcessorText() {

      @Override
      public void commandHandle(TextRequest request) {
        commandProcess(request);
      }
    };
  }

  public void commandProcess(TextRequest request) {}
}
