package test.mock;

import ra.net.NetServiceable;

/** Test class. */
public class NetObject implements NetServiceable {

  @Override
  public void onClose() {}

  @Override
  public boolean getSendCompilete() {
    return false;
  }

  @Override
  public void setSendCompilete(boolean compilete) {}

  @Override
  public void send(String msg) {}

  @Override
  public void sendClose(String msg) {}
}
