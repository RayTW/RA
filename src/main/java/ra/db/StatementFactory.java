package ra.db;

import java.sql.SQLException;
import java.sql.Statement;
import ra.exception.RaConnectException;

/**
 * Statement factory.
 *
 * @author Ray Li
 */
public interface StatementFactory {
  public <T extends Statement> T create(String sql) throws RaConnectException, SQLException;
}
