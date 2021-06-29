package ra.net.processor;

import ra.net.NetService;

/**
 * Process text command.
 *
 * @author Ray Li
 */
public abstract class NetCommandProcessor
    implements CommandProcessorListener<NetService.NetRequest> {

  @Override
  public void commandProcess(NetService.NetRequest request) {}
}
