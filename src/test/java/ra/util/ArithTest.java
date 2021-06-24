package ra.util;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/** Test class. */
public class ArithTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testAdd() {
    double result = Arith.add(0.1, 0.1);

    assertEquals(0.2, result, 0);
  }

  @Test
  public void testDiv() {
    double result = Arith.div(5, 2);

    assertEquals(2.5, result, 0);
  }

  @Test
  public void testDivScaleUsingPositive() {
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("The scale must be a positive integer or zero");

    Arith.div(5, 5, -1);
  }

  @Test
  public void testDivScale() {
    double result = Arith.div(5, 2, 0);

    assertEquals(3.0, result, 0);
  }

  @Test
  public void testFloor() {
    double result = Arith.floor(0.366, 2);

    assertEquals(0.36, result, 0);
  }

  @Test
  public void testFloorScaleUsingPositive() {
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("The scale must be a positive integer or zero");

    Arith.floor(5, -1);
  }

  @Test
  public void testMul() {
    double result = Arith.mul(0.5, 0.5);

    assertEquals(0.25, result, 0);
  }

  @Test
  public void testRound() {
    double result = Arith.round(0.366, 2);

    assertEquals(0.37, result, 0);
  }

  @Test
  public void testRoundScaleUsingPositive() {
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("The scale must be a positive integer or zero");

    Arith.round(5, -1);
  }

  @Test
  public void testSub() {
    double result = Arith.sub(0.366, 0.02);

    assertEquals(0.346, result, 0);
  }

  @Test
  public void testToPlainString() {
    String result = Arith.toPlainString(1234E+4);

    assertEquals("12340000", result);
  }
}
