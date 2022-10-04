package ra.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test class.
 *
 * @author Ray Li
 */
public class PreparedTest {

  @Test
  public void testBuilder() {
    Prepared obj =
        Prepared.newQueryBuilder("SELECT column FROM tableName;")
            .set(1, ParameterValue.string("test"))
            .set(2, ParameterValue.bytes(new byte[2]))
            .set(3, ParameterValue.bool(Boolean.TRUE))
            .build();

    assertEquals("SELECT column FROM tableName;", obj.getSql());
    assertEquals(3, obj.getValues().size());
    assertNotNull(obj.getValues().get(1));
    assertNotNull(obj.getValues().get(2));
    assertNotNull(obj.getValues().get(3));
  }

  @Test
  public void testSqlNullPointerException() {
    try {
      Prepared.newQueryBuilder(null).build();
    } catch (NullPointerException ex) {
      assertNotNull(ex);
    }
  }
}