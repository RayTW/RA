package ra.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.naming.NamingException;
import ra.net.nio.DataNetService;
import ra.net.nio.PackageHandleOutput;
import ra.net.processor.CommandProcessorListener;
import ra.net.processor.CommandProcessorProvider;
import ra.net.processor.DataNetCommandProvider;
import ra.net.processor.NetCommandProvider;
import ra.net.request.Request;
import ra.util.annotation.Configuration;
import ra.util.annotation.ServerApplication;

/**
 * {@link NetService}, {@link DataNetService} manager.
 *
 * @author Ray Li
 */
public class NetServerApplication implements NetServiceProvider {
  private static NetServerApplication instance;

  private static final String NET = "Net";
  private ServerSocket serverSocket;
  private Map<String, Serviceable<?>> servicePool;
  private Map<String, User> userList;
  private MessageSender sender;
  private ExecutorService threadPool;
  private ServerConfiguration configuration;

  /** Initialize. */
  public NetServerApplication() {
    servicePool = new HashMap<>();
    userList = new ConcurrentHashMap<>();
    sender = new MessageSender();
    sender.setNetServiceProvider(this);
  }

  /**
   * User offline.
   *
   * @param index Socket index
   */
  public void removeUser(int index) {
    User user = userList.get("" + index);

    if (user != null) {
      user.close();
      userList.remove("" + index);
    }
  }

  /**
   * Put the user into user pool.
   *
   * @param index index
   * @param listener listener
   */
  public void putUser(int index, User listener) {
    userList.put("" + index, listener);
  }

  /**
   * Put {@link NetService}.
   *
   * @param index service index
   * @param service service
   * @throws NamingException The key already exists
   */
  public void putNetService(int index, NetService service) throws NamingException {
    this.putService(NET + index, service);
  }

  /**
   * Put DataNetService.
   *
   * @param index service index
   * @param service service
   * @throws NamingException NamingException
   */
  public void putDataNetService(int index, DataNetService service) throws NamingException {
    this.putService(NET + index, service);
  }

  /**
   * Put Serviceable.
   *
   * @param index index
   * @param service service
   * @throws NamingException NamingException
   */
  public void putService(int index, Serviceable<?> service) throws NamingException {
    this.putService(NET + index, service);
  }

  /**
   * Put Serviceable.
   *
   * @param key key
   * @param service service
   * @throws NamingException NamingException
   */
  public void putService(String key, Serviceable<?> service) throws NamingException {
    if (servicePool.containsKey(key)) {
      throw new NamingException("Naming repeat, key = " + key);
    }

    servicePool.put(key, service);
  }

  @Override
  public Serviceable<?> getService(int index) {
    return servicePool.get(NET + index);
  }

  /**
   * Returns service.
   *
   * @param key key
   * @return service
   */
  public Serviceable<?> getService(String key) {
    return servicePool.get(key);
  }

  /**
   * Returns service.
   *
   * @param index specify index
   * @return NetService
   */
  public NetService getNetService(int index) {
    return (NetService) servicePool.get(NET + index);
  }

  /**
   * Returns service.
   *
   * @param index specify index
   * @return DataNetService
   */
  public DataNetService getDataNetService(int index) {
    return (DataNetService) servicePool.get(NET + index);
  }

  /**
   * Returns user.
   *
   * @param index specify index
   * @return User
   */
  public User getUser(int index) {
    return userList.get("" + index);
  }

  @Override
  public Map<String, User> getUsers() {
    return userList;
  }

  /**
   * Returns message sender.
   *
   * @return MessageSender
   */
  public MessageSender getMessageSender() {
    return sender;
  }

  /**
   * Returns server configuration.
   *
   * @return ServerConfiguration
   */
  public ServerConfiguration getConfiguration() {
    return configuration;
  }

  /**
   * Get current application.
   *
   * @return NetServerApplication
   */
  public static NetServerApplication getApplication() {
    return instance;
  }

  /**
   * Run server.
   *
   * @param source source
   * @param args args
   */
  @SuppressWarnings("resource")
  public static void run(Class<?> source, String... args) {
    if (!source.isAnnotationPresent(ServerApplication.class)) {
      throw new RuntimeException("Source '" + source + "' is not annotation @ServerApplication.");
    }

    NetServerApplication application = new NetServerApplication();
    ServerConfiguration configuration = null;
    String path = null;

    if (source.isAnnotationPresent(Configuration.class)) {
      Configuration configurationAnnotation = source.getAnnotation(Configuration.class);
      path = configurationAnnotation.value();
    } else {
      throw new RuntimeException("Source '" + source + "' is not annotation @Configuration.");
    }

    try {
      configuration = new ServerConfiguration(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
    application.configuration = configuration;

    int poolSize = application.configuration.getPropertyAsInt("server.netservice.max-threads", 200);
    int port = application.configuration.getPropertyAsInt("server.port", 20000);
    ServerApplication annotation = source.getAnnotation(ServerApplication.class);
    Class<? extends CommandProcessorProvider<? extends Request>> commandProviderClass =
        annotation.serviceMode();
    ServerSocket serverSocket = null;

    System.out.printf(
        "port=%d, poolsize=%d, commandProvider=%s\n",
        port, poolSize, commandProviderClass.getName());

    try {
      serverSocket = new ServerSocket(port, poolSize);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(
          "ServerSocket not initialize, port = " + port + ",poolSize=" + poolSize + ".", e);
    }

    CommandProcessorProvider<? extends Request> providerObject = null;
    try {
      providerObject = commandProviderClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "The provider class '" + commandProviderClass + "' constructor is a mismatch.");
    }
    if (NetCommandProvider.class.isAssignableFrom(commandProviderClass)) {
      application.runNetService(serverSocket, port, poolSize, (NetCommandProvider) providerObject);
    } else if (DataNetCommandProvider.class.isAssignableFrom(commandProviderClass)) {
      application.runNetDataService(
          serverSocket, port, poolSize, (DataNetCommandProvider) providerObject);
    } else {
      throw new RuntimeException(
          "The provider class '"
              + commandProviderClass
              + "' invalid, please use or inherit NetServiceCommandProvider, "
              + "DataNetServiceCommandProvider.");
    }
    instance = application;
  }

  /**
   * NetServerApplication initialize.
   *
   * @param serverSocket serverSocket
   * @param port port
   * @param poolSize request pool size
   * @param providerClass command provider
   */
  private void runNetService(
      ServerSocket serverSocket, int port, int poolSize, NetCommandProvider commandProvider) {
    this.serverSocket = serverSocket;
    threadPool = Executors.newFixedThreadPool(poolSize);

    NetService.Builder builder = new NetService.Builder();
    NetService service = null;

    builder
        .setSendExecutor(threadPool)
        .setSocketSoTimeout(Duration.ofSeconds(20))
        .setServerSocket(serverSocket);

    for (int i = 1; i <= poolSize; i++) {
      builder.setIndex(i);
      service = builder.build();
      service.setCommandProcessorProvider(
          new NetCommandProvider() {
            @Override
            public CommandProcessorListener<NetService.NetRequest> createCommand() {
              return commandProvider.createCommand();
            }

            @Override
            public void offline(int index) {
              commandProvider.offline(index);
              removeUser(index);
            }
          });

      try {
        putNetService(i, service);
      } catch (NamingException e) {
        e.printStackTrace();
      }
      service.start();
    }
  }

  /**
   * NetServerApplication initialize.
   *
   * @param serverSocket serverSocket
   * @param port port
   * @param poolSize request pool size
   * @param providerClass command provider
   */
  private void runNetDataService(
      ServerSocket serverSocket, int port, int poolSize, DataNetCommandProvider commandProvider) {
    this.serverSocket = serverSocket;
    threadPool = Executors.newFixedThreadPool(poolSize);

    DataNetService.Builder builder =
        new DataNetService.Builder()
            .setSendExecutor(threadPool)
            .setServerSocket(serverSocket)
            .setCommandProcessorProvider(
                new DataNetCommandProvider() {
                  @Override
                  public CommandProcessorListener<DataNetService.DataNetRequest> createCommand() {
                    return commandProvider.createCommand();
                  }

                  @Override
                  public void offline(int index) {
                    commandProvider.offline(index);
                    removeUser(index);
                  }
                })
            .setSocketSoTimeout(Duration.ofSeconds(60))
            .setTransferListener(new PackageHandleOutput());
    DataNetService service = null;

    for (int i = 1; i <= poolSize; i++) {
      builder.setIndex(i);
      service = builder.build();
      try {
        putDataNetService(i, service);
      } catch (NamingException e) {
        e.printStackTrace();
      }
      service.start();
    }
  }

  /** Finish application. */
  public void close() {
    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    threadPool.shutdownNow();
    servicePool
        .values()
        .stream()
        .filter(service -> AutoCloseable.class.isInstance(service))
        .map(AutoCloseable.class::cast)
        .forEach(
            service -> {
              try {
                service.close();
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
    userList.values().forEach(user -> user.close());
  }
}
