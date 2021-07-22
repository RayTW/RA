package ra.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import ra.db.parameter.H2Parameters;
import ra.db.parameter.MysqlParameters;
import ra.ref.Reference;

/** Test class. */
public class OnceDatabaseFactoryTest {

  @Test
  public void testNewInstance() {
    MysqlParameters.Builder builder = new MysqlParameters.Builder();

    builder.setHost("");

    OnceDatabaseFactory factory = new OnceDatabaseFactory(builder.build());

    assertNotNull(factory);
  }

  @Test
  public void testNewInstanceSupplier() {
    MysqlParameters.Builder builder = new MysqlParameters.Builder();

    builder.setHost("");

    OnceDatabaseFactory factory = new OnceDatabaseFactory(() -> builder.build());

    assertNotNull(factory);
  }

  @Test
  public void testGetCount() {
    MysqlParameters.Builder builder = new MysqlParameters.Builder();

    builder.setHost("");

    OnceDatabaseFactory factory = new OnceDatabaseFactory(builder.build());

    assertEquals(0, factory.getCount());
  }

  @Test
  public void testGetAndClose() {
    Reference<StatementExecutor> ref = new Reference<>();

    new OnceDatabaseFactory(new H2Parameters.Builder().inMemory().setName("test")::build)
        .getAndClose(
            executor -> {
              executor.executeQuery("SELECT 1");
              ref.set(executor);
            });

    assertFalse(ref.get().isLive());
  }
}
