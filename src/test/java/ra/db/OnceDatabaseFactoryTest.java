package ra.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import ra.db.parameter.MysqlParameters;

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
}
