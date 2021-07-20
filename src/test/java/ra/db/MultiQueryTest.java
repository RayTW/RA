package ra.db;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import org.junit.Test;
import ra.db.record.RecordSet;
import ra.ref.BooleanReference;

/** Test class. */
public class MultiQueryTest {

  @Test
  public void testClose() throws SQLException {
    BooleanReference ref = new BooleanReference(false);
    MultiQuery query =
        new MultiQuery(() -> new RecordSet(DatabaseCategory.MYSQL), new MockStatement()) {
          @Override
          public void close() {
            super.close();
            ref.set(true);
          }
        };

    query.close();

    assertTrue(ref.get());
  }
}
