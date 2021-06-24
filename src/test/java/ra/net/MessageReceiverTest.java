package ra.net;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/** Test class. */
public class MessageReceiverTest {

  @Test
  public void testClose() {
    MessageReceiver obj =
        new MessageReceiver(
            (msg) -> {
              System.out.println(msg);
              try {
                TimeUnit.SECONDS.sleep(3);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            });
    obj.start();
    obj.close();
    assertFalse(obj.isRun());
  }

  @Test
  public void testPut() {
    ArrayList<String> commands = new ArrayList<>();
    commands.add("aaa");
    commands.add("bbb");
    commands.add("ccc");
    CountDownLatch lock = new CountDownLatch(commands.size());

    MessageReceiver obj =
        new MessageReceiver(
            (bytes) -> {
              int index = commands.indexOf(new String(bytes));

              if (index != -1) {
                commands.remove(index);
              }

              lock.countDown();
            });
    obj.start();

    commands
        .stream()
        .forEach(
            (str) -> {
              obj.put(str);
            });
    obj.awake();
    try {
      lock.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    assertTrue(commands.isEmpty());
  }

  @Test
  public void testPutAndAwake() {
    CopyOnWriteArrayList<String> commands = new CopyOnWriteArrayList<>();
    for (int i = 0; i < 1000; i++) {
      commands.add("command[" + i + "]");
    }

    CountDownLatch lock = new CountDownLatch(commands.size());

    MessageReceiver obj =
        new MessageReceiver(
            (bytes) -> {
              int index = commands.indexOf(new String(bytes));

              if (index != -1) {
                commands.remove(index);
              }

              lock.countDown();
            });
    obj.start();

    commands
        .stream()
        .forEach(
            (str) -> {
              obj.put(str);
            });
    obj.awake();
    try {
      lock.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    assertTrue(commands.isEmpty());
  }
}
