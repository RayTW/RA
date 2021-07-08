package ra.net;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;
import ra.net.processor.NetCommandProvider;
import ra.ref.Reference;
import test.UnitTestUtils;
import test.mock.MockNetServiceCommand;

/** Test class. */
public class NetServiceTest {

  @Test
  public void testInitialize() throws IOException, InterruptedException {
    int backlog = 1;
    ExecutorService threadPool = Executors.newFixedThreadPool(backlog);
    ServerSocket serverSocket = UnitTestUtils.generateServerSocket(backlog);

    NetService.Builder builder = new NetService.Builder();
    NetService service = null;

    builder
        .setSocketSoTimeout(Duration.ofSeconds(20))
        .setSendExecutor(threadPool)
        .setServerSocket(serverSocket);

    builder.setIndex(1);
    service = builder.build();
    service.setCommandProcessorProvider(new MockNetServiceCommand());
    service.setSendCompilete(false); // Meaningless
    service.start();

    Thread.sleep(100);
    service.close();
    serverSocket.close();
    threadPool.shutdown();

    assertNotNull(service);
    assertEquals(1, service.getIndex());
    assertFalse(service.getSendCompilete());
  }

  @Test
  public void testSendTextToClient() throws IOException {
    int backlog = 1;
    ServerSocket serverSocket = UnitTestUtils.generateServerSocket(backlog);
    NetService.Builder builder = new NetService.Builder();
    ExecutorService threadPool = Executors.newFixedThreadPool(backlog);

    builder.setSendExecutor(threadPool);
    builder.setSocketSoTimeout(Duration.ofSeconds(20)).setServerSocket(serverSocket);
    builder.setIndex(0);

    NetService service = builder.build();

    service.setCommandProcessorProvider(
        new NetCommandProvider() {
          @Override
          public void receivedRequest(NetService.NetRequest request) {
            request.getSender().send(request.getText());
          }
        });

    service.start();
    String expected = "test";

    NetSocketWriter socket =
        new NetSocketWriter.Builder()
            .setHost("127.0.0.1")
            .setPort(serverSocket.getLocalPort())
            .setSendTimeOut(5000)
            .build();

    String actual = socket.send(expected);

    assertEquals(expected, actual);

    serverSocket.close();
    threadPool.shutdownNow();
    service.close();
  }

  @Test
  public void testSendCloseToClient() throws IOException {
    int backlog = 1;
    ServerSocket serverSocket = UnitTestUtils.generateServerSocket(backlog);
    NetService.Builder builder = new NetService.Builder();
    ExecutorService threadPool = Executors.newFixedThreadPool(backlog);

    builder.setSendExecutor(threadPool);
    builder.setSocketSoTimeout(Duration.ofSeconds(20)).setServerSocket(serverSocket);
    builder.setIndex(0);

    NetService service = builder.build();

    service.setCommandProcessorProvider(
        new NetCommandProvider() {
          @Override
          public void receivedRequest(NetService.NetRequest request) {
            request.getSender().sendClose(request.getText());
          }
        });

    service.start();

    NetSocketWriter socket =
        new NetSocketWriter.Builder()
            .setHost("127.0.0.1")
            .setPort(serverSocket.getLocalPort())
            .setSendTimeOut(5000)
            .build();

    String expected = "test";
    String actual = socket.send(expected);

    assertEquals(expected, actual);

    serverSocket.close();
    threadPool.shutdownNow();
    service.close();
  }

  @Test
  public void testSendAsync() throws IOException, InterruptedException {
    int backlog = 1;
    ServerSocket serverSocket = UnitTestUtils.generateServerSocket(backlog);
    NetService.Builder builder = new NetService.Builder();
    ExecutorService threadPool = Executors.newFixedThreadPool(backlog);

    builder.setSendExecutor(threadPool);
    builder.setSocketSoTimeout(Duration.ofSeconds(20)).setServerSocket(serverSocket);
    builder.setIndex(0);

    NetService service = builder.build();
    Reference<String> actual = new Reference<>();
    CountDownLatch letch = new CountDownLatch(1);

    service.setCommandProcessorProvider(
        new NetCommandProvider() {
          @Override
          public void receivedRequest(NetService.NetRequest request) {
            request.getSender().sendClose(request.getText());
            actual.set(request.getText());
            letch.countDown();
          }
        });

    service.start();

    String expected = "ttest11";

    NetSocketWriter socket =
        new NetSocketWriter.Builder()
            .setHost("127.0.0.1")
            .setPort(serverSocket.getLocalPort())
            .setSendTimeOut(5000)
            .build();
    socket.sendAsync(expected);

    letch.await();
    serverSocket.close();
    threadPool.shutdownNow();
    service.close();

    assertEquals(expected, actual.get());
  }

  @Test
  public void testSendCloseToKeepClient2message() throws IOException, InterruptedException {
    int backlog = 1;
    ServerSocket serverSocket = UnitTestUtils.generateServerSocket(backlog);
    NetService.Builder builder = new NetService.Builder();
    ExecutorService threadPool = Executors.newFixedThreadPool(backlog);

    builder.setSendExecutor(threadPool);
    builder.setSocketSoTimeout(Duration.ofSeconds(20)).setServerSocket(serverSocket);
    builder.setIndex(0);

    NetService service = builder.build();

    // server
    service.setCommandProcessorProvider(
        new NetCommandProvider() {
          @Override
          public void receivedRequest(NetService.NetRequest request) {
            request.getSender().sendClose(request.getText());
          }
        });

    service.start();
    CopyOnWriteArrayList<String> actual = new CopyOnWriteArrayList<>();
    CountDownLatch letch = new CountDownLatch(1);

    // client
    NetSocketWriterKeep socket =
        new NetSocketWriterKeep.Builder()
            .setHost("127.0.0.1")
            .setPort(serverSocket.getLocalPort())
            .setIndex(0)
            .setCommandProcessorProvider(
                new NetCommandProvider() {

                  @Override
        public void receivedRequest(NetService.NetRequest request) {
                    actual.add(request.getText());
                    letch.countDown();
                  }
                })
            .build();
    socket.connect();

    String expected =
        "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttes"
            + "ttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
            + "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttestt"
            + "esttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttestte"
            + "sttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttes"
            + "ttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
            + "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttestt"
            + "esttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttestte"
            + "sttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttes"
            + "ttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
            + "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttestt"
            + "esttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttestte"
            + "sttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttes"
            + "ttesttesttesttesttesttesttesttesttesttesttest";

    socket.send(expected);
    letch.await();
    serverSocket.close();
    socket.close();
    threadPool.shutdownNow();

    assertThat(actual, hasItems(expected));
  }
}
