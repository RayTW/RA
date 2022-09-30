package ra.db;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import org.junit.Test;

/**
 * Test class.
 *
 * @author Ray Li
 */
public class ParameterValueTest {

  @Test
  public void testInt64() {
    ParameterValue value = ParameterValue.int64(8L);

    assertEquals("8", value.getValue().toString());
    assertEquals(Long.class, value.getType());
  }

  @Test
  public void testInt64FromInteger() {
    ParameterValue value = ParameterValue.int64(8);

    assertEquals("8", value.getValue().toString());
    assertEquals(Integer.class, value.getType());
  }

  @Test
  public void testBigNumeric() {
    ParameterValue value = ParameterValue.bigNumeric(new BigDecimal("8.888"));

    assertEquals("8.888", value.getValue().toString());
    assertEquals(BigDecimal.class, value.getType());
  }

  @Test
  public void testBlob() throws SerialException, SQLException {
    byte[] byteArray = "test".getBytes();
    Blob blob = new SerialBlob(byteArray);
    ParameterValue value = ParameterValue.blob(blob);

    assertEquals(Blob.class, value.getType());
    assertArrayEquals(byteArray, ((SerialBlob) value.getValue()).getBytes(1, 4));
  }

  @Test
  public void testBytes() throws SerialException, SQLException {
    byte[] byteArray = "abcd".getBytes();
    ParameterValue value = ParameterValue.bytes(byteArray);

    assertEquals(byte[].class, value.getType());
    assertArrayEquals(byteArray, (byte[]) value.getValue());
  }

  @Test
  public void testBoolenAsTrue() {
    ParameterValue value = ParameterValue.bool(true);

    assertEquals("true", value.getValue().toString());
    assertEquals(Boolean.class, value.getType());
  }

  @Test
  public void testBoolenAsFalse() {
    ParameterValue value = ParameterValue.bool(false);

    assertEquals("false", value.getValue().toString());
    assertEquals(Boolean.class, value.getType());
  }

  @Test
  public void testBoolenFromeDouble() {
    ParameterValue value = ParameterValue.float64(8.123d);

    assertEquals("8.123", value.getValue().toString());
    assertEquals(Double.class, value.getType());
  }

  @Test
  public void testBoolenFromeFloat() {
    ParameterValue value = ParameterValue.float64(8.123f);

    assertEquals("8.123", value.getValue().toString());
    assertEquals(Float.class, value.getType());
  }

  @Test
  public void testString() {
    ParameterValue value = ParameterValue.string("testString");

    assertEquals("testString", value.getValue().toString());
    assertEquals(String.class, value.getType());
  }

  @Test
  public void testOfFromObject() {
    ParameterValue value = ParameterValue.of(new Object(), Object.class);

    assertNotNull(value);
    assertEquals(Object.class, value.getType());
  }

  @Test
  public void testTypeNullPointerException() {
    try {
      ParameterValue.newBuilder().setValue("test").setType(null).build();
    } catch (NullPointerException ex) {
      assertNotNull(ex);
    }
  }
}
