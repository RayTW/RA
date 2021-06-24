package ra.util;

import org.junit.Assert;
import org.junit.Test;

/** Test class. */
public class SpaceUnitTest {

  @Test
  public void testMbConvertToBytes() {
    // 10mb to bytes
    long value = SpaceUnit.Bytes.convert(10, SpaceUnit.MB);

    Assert.assertEquals(10485760, value);
  }

  @Test
  public void testKbConvertToBytes() {
    long value = SpaceUnit.Bytes.convert(5, SpaceUnit.KB);

    Assert.assertEquals(5120, value);
  }

  @Test
  public void testBytesConvertToBytes() {
    long value = SpaceUnit.Bytes.convert(1100, SpaceUnit.Bytes);

    Assert.assertEquals(1100, value);
  }

  @Test
  public void testBytesConvertToKb() {
    long value = SpaceUnit.KB.convert(2048, SpaceUnit.Bytes);

    Assert.assertEquals(2, value);
  }

  @Test
  public void testBytesConvertToMb() {
    long value = SpaceUnit.MB.convert(10485760, SpaceUnit.Bytes);

    Assert.assertEquals(10, value);
  }

  @Test
  public void testMbConvertToGb() {
    long value = SpaceUnit.GB.convert(2048, SpaceUnit.MB);

    Assert.assertEquals(2, value);
  }

  @Test
  public void testGbConvertToTb() {
    long value = SpaceUnit.TB.convert(1024, SpaceUnit.GB);

    Assert.assertEquals(1, value);
  }

  @Test
  public void testGbConvertDecimalToTb() {
    double value = SpaceUnit.TB.convertDecimal(1500, SpaceUnit.GB);

    Assert.assertEquals(1.46484375, value, 0);
  }
}
