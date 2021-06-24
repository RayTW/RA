package ra.net.nio;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/** Test class. */
public class DataTypeTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testValueOfUsingText() {
    DataType type = DataType.valueOf(0x0000);

    assertEquals(DataType.TEXT, type);
  }

  @Test
  public void testValueOfUsingZip() {
    DataType type = DataType.valueOf(0x0010);

    assertEquals(DataType.ZIP, type);
  }

  @Test
  public void testCopyToBytes() {
    byte[] data = new byte[2];
    DataType.copyToBytes(data, DataType.ZIP.getType());

    assertArrayEquals(new byte[] {0x00, 0x10}, data);
  }

  @Test
  public void testToInt() {
    assertEquals(DataType.ZIP.getType(), DataType.toInt(new byte[] {0x00, 0x10}));
  }

  @Test
  public void testValueOfUsingUnsupport() {
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("no match type =-1");

    DataType.valueOf(0xffffffff);
  }
}
