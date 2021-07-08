package ra.net.processor;

import ra.net.nio.DataNetService;

/**
 * Process data command.
 *
 * @author Ray Li
 */
public abstract class DataNetCommandProcessor
    implements CommandProcessorListener<DataNetService.DataNetRequest> {

  @Override
  public void commandProcess(DataNetService.DataNetRequest request) {}
}
