package ra.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Test;

/** Test class. */
public class SimpleDateFormatThreadSafeTest {

  @Test
  public void testNewInstance() {
    SimpleDateFormatThreadSafe obj = new SimpleDateFormatThreadSafe();

    assertNotNull(obj);
  }

  @Test
  public void testNewInstanceUsingTimeZone() {
    TimeZone expected = TimeZone.getDefault();
    SimpleDateFormatThreadSafe obj =
        new SimpleDateFormatThreadSafe("yyyy-MM-dd HH:mm:ss.SSS", expected);

    assertSame(expected, obj.getTimeZone());
  }

  @Test
  public void testNewInstanceUsingLocale() {
    SimpleDateFormatThreadSafe obj =
        new SimpleDateFormatThreadSafe("yyyy-MM-dd HH:mm:ss.SSS", Locale.TAIWAN);
    assertNotNull(obj);
  }

  @Test
  public void testNewInstanceUsingPattern() {
    SimpleDateFormatThreadSafe obj = new SimpleDateFormatThreadSafe("yyyy-MM-dd HH:mm:ss.SSS");

    String now = obj.format(new Date());

    assertNotNull(now);
  }

  @Test
  public void testSetGetCalendar() {
    SimpleDateFormatThreadSafe obj = new SimpleDateFormatThreadSafe("yyyy-MM-dd HH:mm:ss.SSS");
    Calendar expected = Calendar.getInstance();

    obj.setCalendar(expected);

    assertSame(expected, obj.getCalendar());
  }

  @Test
  public void testParse() throws ParseException {
    SimpleDateFormatThreadSafe obj = new SimpleDateFormatThreadSafe("yyyy-MM-dd HH:mm:ss.SSS");

    long time = obj.parse("2020-04-10 15:42:50.770").getTime();

    System.out.println("time[" + time + "],1586504570770L");
    assertEquals(1586504570770L, time);
  }

  @Test
  public void testToPattern() {
    SimpleDateFormatThreadSafe obj = new SimpleDateFormatThreadSafe("yyyy-MM-dd HH:mm:ss.SSS");

    assertEquals("yyyy-MM-dd HH:mm:ss.SSS", obj.toPattern());
  }

  @Test
  public void testApplyPattern() {
    SimpleDateFormatThreadSafe obj = new SimpleDateFormatThreadSafe();

    obj.applyPattern("yyyy-MM-dd");
    assertEquals("yyyy-MM-dd", obj.toPattern());
  }
}
