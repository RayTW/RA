package ra.db;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import ra.ref.BooleanReference;

/** Test class. */
public class DatabaseHeartbeatTest {

  @Test
  public void testTnterrupt() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    BooleanReference isDone = new BooleanReference();
    DatabaseHeartbeat obj =
        new DatabaseHeartbeat(
            new KeepAlive() {

              @Override
              public long interval() {
                return 2000;
              }

              @Override
              public void keep() {
                isDone.set(true);
                countDownLatch.countDown();
              }
            }) {};

    obj.start();

    obj.interrupt();
    countDownLatch.await();

    assertTrue(isDone.get());
  }
}
