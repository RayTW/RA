package ra.net;

import java.util.Map;

/**
 * Tool class of sent message.
 *
 * @author Ray Li, Kevin Tasi
 */
public class MessageSender {
  private NetServiceProvider serviceProvider;

  /**
   * Set NetServiceProvider.
   *
   * @param serviceProvider serviceProvider
   */
  public void setNetServiceProvider(NetServiceProvider serviceProvider) {
    this.serviceProvider = serviceProvider;
  }

  /**
   * Broadcast message to all users.
   *
   * @param message message
   */
  public void broadcast(String message) {
    broadcast(message, this.serviceProvider.getUsers());
  }

  /**
   * Broadcast message to all users.
   *
   * @param <T> {@link User}
   * @param message message
   * @param userlist user list
   */
  @SuppressWarnings("unchecked")
  public <T extends User> void broadcast(String message, Map<String, T> userlist) {
    userlist
        .entrySet()
        .parallelStream()
        .map(
            entry -> {
              Serviceable<String> net =
                  (Serviceable<String>)
                      this.serviceProvider.getService(entry.getValue().getIndex());

              return net;
            });
  }

  /**
   * Sent message specific user.
   *
   * @param message message
   * @param index specific user
   */
  @SuppressWarnings("unchecked")
  public void send(String message, int index) {
    Serviceable<String> net = (Serviceable<String>) this.serviceProvider.getService(index);

    try {
      net.send(message);
    } catch (Exception e) {
      e.printStackTrace();
      net.onClose();
    }
  }

  /**
   * After sending messages to specific user will close user connection.
   *
   * @param message message
   * @param index specific user
   */
  @SuppressWarnings("unchecked")
  public void sendClose(String message, int index) {
    Serviceable<String> net = (Serviceable<String>) this.serviceProvider.getService(index);

    try {
      net.sendClose(message);
    } catch (Exception e) {
      net.onClose();
      e.printStackTrace();
    }
  }
}
