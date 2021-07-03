package ra.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

/** Test class. */
public class Iso8601Test {

  @Test
  public void testNowNoColon() {
    String now = Iso8601.now(false);

    System.out.println(now);

    Assert.assertTrue(now != null);
  }

  @Test
  public void testToCalendarNoColon() throws ParseException {
    long expected = 1542245787000L;
    Calendar calendar = Iso8601.toCalendar("2018-11-15T09:36:27+0800");
    long actual = calendar.getTime().getTime();

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testNow() {
    String now = Iso8601.now();

    System.out.println(now);

    Assert.assertTrue(now != null);
  }

  @Test
  public void testFromDate() throws ParseException {
    String expected = "2018-11-15T09:36:27+08:00";
    Date date = new Date(1542245787000L);
    String actual = Iso8601.fromDate(date);

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testFromDateNoColon() throws ParseException {
    String expected = "2018-11-15T09:36:27+0800";
    Date date = new Date(1542245787000L);
    String actual = Iso8601.fromDate(date, false);

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testToDate() throws ParseException {
    long expected = 1542245787000L;
    Date date = Iso8601.toDate("2018-11-15T09:36:27+08:00");
    long actual = date.getTime();

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testToCalendar() throws ParseException {
    long expected = 1542245787000L;
    Calendar calendar = Iso8601.toCalendar("2018-11-15T09:36:27+08:00");
    long actual = calendar.getTime().getTime();

    Assert.assertEquals(expected, actual);
  }
}
