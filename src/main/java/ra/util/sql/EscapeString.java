package ra.util.sql;

/**
 * Ray Li(modified)
 *
 * <p>Mysql util.
 *
 * @author Ralph Ritoch rritoch@gmail.com copyright Ralph Ritoch 2011 ALL RIGHTS RESERVED
 *     http://www.vnetpublishing.com
 */
public class EscapeString {

  /**
   * Escape string to protected against SQL Injection
   *
   * <p>You must add a single quote ' around the result of this function for data, or a backtick `
   * around table and row identifiers. If this function returns null than the result should be
   * changed to "NULL" without any quote or backtick.
   *
   * @param str Waiting to protect against SQL Injection`s String
   * @return finished to protect against SQL Injection`s String
   */
  public static String mysqlRealEscapeString(String str) {
    if (str == null) {
      return null;
    }

    if (str.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]", "").length() < 1) {
      return str;
    }

    String cleanString = str;
    cleanString = cleanString.replaceAll("\\\\", "\\\\\\\\");
    cleanString = cleanString.replaceAll("\\n", "\\\\n");
    cleanString = cleanString.replaceAll("\\r", "\\\\r");
    cleanString = cleanString.replaceAll("\\t", "\\\\t");
    cleanString = cleanString.replaceAll("\\00", "\\\\0");
    cleanString = cleanString.replaceAll("'", "\\\\'");
    cleanString = cleanString.replaceAll("\\\"", "\\\\\"");

    return cleanString;
  }

  /**
   * Escape data to protected against SQL Injection.
   *
   * @param str Waiting to protect against SQL Injection`s String
   * @return finished to protect against SQL Injection`s String
   * @throws Exception Exception
   */
  public static String quote(String str) throws Exception {
    if (str == null) {
      return "NULL";
    }
    return "'" + mysqlRealEscapeString(str) + "'";
  }

  /**
   * Escape identifier to protected against SQL Injection.
   *
   * @param str Waiting to protect against SQL Injection`s String
   * @return finished to protect against SQL Injection`s String
   * @throws Exception .
   */
  public static String nameQuote(String str) throws Exception {
    if (str == null) {
      return "NULL";
    }
    return "`" + mysqlRealEscapeString(str) + "`";
  }
}
