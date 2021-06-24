package ra.net.nio;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import ra.net.NetServerApplication;
import ra.net.processor.DataNetCommandProvider;
import ra.net.request.DataRequest;
import ra.ref.Reference;
import ra.util.annotation.Configuration;
import test.UnitTestUtils;
import test.mock.annotationclass.TestReadText;
import test.mock.annotationclass.TestReadZip;

/** Test class. */
public class DataNetServiceTest {

  @Test
  public void testCloseNotStart() throws IOException {
    ServerSocket serverSocket = UnitTestUtils.generateServerSocket();
    DataNetService net =
        new DataNetService.Builder()
            .setIndex(0)
            .setServerSocket(serverSocket)
            .setCommandProcessorProvider(generateCmdProcProvider())
            .setSocketSoTimeout(Duration.ofSeconds(60))
            .setTransferListener((data, listener) -> {})
            .build();

    net.close();
    serverSocket.close();

    assertNotNull(net);
  }

  @Test
  public void testCloseStart() {
    ServerSocket serverSocket = UnitTestUtils.generateServerSocket();
    ExecutorService pool = Executors.newFixedThreadPool(1);
    DataNetService net =
        new DataNetService.Builder()
            .setSendExecutor(pool)
            .setIndex(0)
            .setServerSocket(serverSocket)
            .setCommandProcessorProvider(generateCmdProcProvider())
            .setSocketSoTimeout(Duration.ofSeconds(60))
            .setTransferListener((data, listener) -> {})
            .build();
    net.start();

    DataSocket socket = new DataSocket();

    socket.connect("127.0.0.1", serverSocket.getLocalPort());

    try {
      socket.close();
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      net.close();
    }
    pool.shutdownNow();

    assertEquals(0, net.getIndex());
  }

  @Test
  public void testReadJsonObject() throws InterruptedException {
    CountDownLatch letch = new CountDownLatch(1);
    final Reference<String> messageRef = new Reference<>();
    ExecutorService pool = Executors.newFixedThreadPool(1);
    ServerSocket serverSocket = UnitTestUtils.generateServerSocket();
    DataNetService net =
        new DataNetService.Builder()
            .setIndex(0)
            .setSendExecutor(pool)
            .setServerSocket(serverSocket)
            .setCommandProcessorProvider(
                generateCommandProvider(
                    (request) -> {
                      messageRef.set(new String(request.getData().getRaw()));
                      letch.countDown();
                    }))
            .setSocketSoTimeout(Duration.ofSeconds(60))
            .setTransferListener((data, listener) -> {})
            .build();
    net.start();

    DataSocket socket = new DataSocket();

    socket.connect("127.0.0.1", serverSocket.getLocalPort());

    JSONObject json = new JSONObject();

    json.put("key", "value");

    socket.write(json);

    letch.await();
    try {
      socket.close();
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      net.close();
    }
    pool.shutdownNow();

    assertEquals("{\"key\":\"value\"}", messageRef.get());
  }

  @Test
  public void testReadJsonArray() throws InterruptedException {
    CountDownLatch letch = new CountDownLatch(1);
    Reference<String> messageRef = new Reference<>();
    ExecutorService pool = Executors.newFixedThreadPool(1);
    ServerSocket serverSocket = UnitTestUtils.generateServerSocket();
    DataNetService net =
        new DataNetService.Builder()
            .setIndex(0)
            .setSendExecutor(pool)
            .setServerSocket(serverSocket)
            .setCommandProcessorProvider(
                generateCommandProvider(
                    (request) -> {
                      messageRef.set(new String(request.getData().getRaw()));
                      letch.countDown();
                    }))
            .setSocketSoTimeout(Duration.ofSeconds(60))
            .setTransferListener((data, listener) -> {})
            .build();
    net.start();

    DataSocket socket = new DataSocket();

    socket.connect("127.0.0.1", serverSocket.getLocalPort());

    JSONArray json = new JSONArray();

    json.put("value");

    socket.write(json);

    letch.await();
    try {
      socket.close();
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      net.close();
    }
    pool.shutdownNow();
    System.out.println("src=" + json.toString().length() + ",re=" + messageRef.get().length());
    assertEquals(json.toString(), messageRef.get());
  }

  @Test
  public void testReadText() throws InterruptedException, IOException {
    int port = runApplication(TestReadText.class);

    Reference<String> resultRef = new Reference<>();
    CountDownLatch letch = new CountDownLatch(1);

    DataSocket socket = new DataSocket();

    socket.connect("127.0.0.1", port);

    socket.setOnReadLineListener(
        data -> {
          resultRef.set(new String(data.getRaw()));
          letch.countDown();
        });

    String expected = "hello, Ray!";

    socket.write(expected);
    letch.await();
    NetServerApplication.getApplication().close();

    assertEquals(expected, resultRef.get());
  }

  @Test
  public void testReadZip() throws InterruptedException, IOException {
    int port = runApplication(TestReadZip.class);

    Reference<byte[]> resultRef = new Reference<>();
    CountDownLatch letch = new CountDownLatch(1);
    String filePath = "unittest/testZip.properties";
    DataSocket socket = new DataSocket();

    socket.connect("127.0.0.1", port);

    socket.setOnReadLineListener(
        data -> {
          resultRef.set(data.getRaw());
          letch.countDown();
        });
    socket.writeFile(Paths.get(filePath));
    letch.await();

    assertArrayEquals(Files.readAllBytes(Paths.get(filePath)), resultRef.get());
  }

  private DataNetCommandProvider generateCmdProcProvider() {
    return generateCommandProvider(null);
  }

  private DataNetCommandProvider generateCommandProvider(Consumer<DataRequest> consumer) {
    DataNetCommandProvider provider =
        new DataNetCommandProvider() {
          @Override
          public void receivedRequest(DataRequest request) {
            consumer.accept(request);
          }
        };

    return provider;
  }

  /**
   * run application.
   *
   * @param application application
   * @return port
   * @throws IOException IOException
   */
  public int runApplication(Class<?> application) throws IOException {
    ServerSocket serverSocket = UnitTestUtils.generateServerSocket();
    int serverPort = serverSocket.getLocalPort();
    serverSocket.close();

    Path path =
        UnitTestUtils.createTempPropertiesFile(
            application.getAnnotation(Configuration.class).value(),
            properties -> {
              properties.put("server.netservice.max-threads", "1");
              properties.put("server.port", String.valueOf(serverPort));
            });
    NetServerApplication.run(application);
    Files.delete(path);

    return serverPort;
  }
}
