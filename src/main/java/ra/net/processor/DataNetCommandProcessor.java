package ra.net.processor;

import ra.net.nio.DataNetService;

/**
 * Process data command.
 *
 * @author Ray Li
 */
public abstract class DataNetCommandProcessor
    implements CommandProcessorListener<DataNetService.NetDataRequest> {

  @Override
  public void commandProcess(DataNetService.NetDataRequest request) {}
}
