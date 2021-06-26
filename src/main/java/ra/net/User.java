package ra.net;

/** User. */
public class User implements UserListener {
  private int index = -1;
  private String ip;
  private String name = "";

  @Override
  public int getIndex() {
    return this.index;
  }

  @Override
  public void setIndex(int index) {
    this.index = index;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setName(String username) {
    this.name = username;
  }

  @Override
  public String getIp() {
    return this.ip;
  }

  @Override
  public void setIp(String ip) {
    this.ip = ip;
  }

  @Override
  public void close() {
    ip = null;
    name = null;
  }
}
