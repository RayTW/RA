package ra.util.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/** Test class. */
public class EscapeStringTest {

  @Test
  public void testSqlInjectionUsingDoubleQuotationMarks() {
    String fieldValue = "\"name\"";

    // actual = \"name\"
    String actual = EscapeString.mysqlRealEscapeString(fieldValue);

    assertEquals("\\\"name\\\"", actual);
  }

  @Test
  public void testSqlInjectionUsingDoubleQuotationMarksIsNull() {
    String actual = EscapeString.mysqlRealEscapeString(null);

    assertNull(actual);
  }

  @Test
  public void testSqlInjectionQuote() throws Exception {
    // actual = 'value123'
    String actual = EscapeString.quote("value123");

    assertEquals("'value123'", actual);
  }

  @Test
  public void testSqlInjectionQuoteIsNull() throws Exception {
    // actual = 'NULL'
    String actual = EscapeString.quote(null);

    assertEquals("NULL", actual);
  }

  @Test
  public void testSqlInjectionNameQuote() throws Exception {
    String actual = EscapeString.nameQuote("name");

    assertEquals("`name`", actual);
  }

  @Test
  public void testSqlInjectionNameQuoteIsNull() throws Exception {
    String actual = EscapeString.nameQuote(null);

    assertEquals("NULL", actual);
  }
}
