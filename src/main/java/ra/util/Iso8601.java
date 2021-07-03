package ra.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Helper class for handling a most common subset of ISO 8601 strings (in the following format:
 * "2008-03-01T13:00:00+01:00"). It supports parsing the "Z" timezone, but many other less-used
 * features are missing.
 *
 * @author Ray Li(editor)
 */
public final class Iso8601 {
  private static final SimpleDateFormatThreadSafe dateFormate =
      new SimpleDateFormatThreadSafe("yyyy-MM-dd'T'HH:mm:ssZ", TimeZone.getTimeZone("Asia/Taipei"));

  /**
   * Transform Date to ISO 8601 string.
   *
   * @param date date
   * @return data string
   */
  public static String fromDate(Date date) {
    return fromDate(date, true);
  }

  /**
   * Transform Date to ISO 8601 string.<br>
   * When colon == true, return format, etc : "2018-11-15T09:36:27+08:00"<br>
   * When colon == false, return format, etc : "2018-11-15T09:36:27+0800"
   *
   * @param date date
   * @param colon true: zone append ":"，false: zone not append ":"
   * @return data string
   */
  public static String fromDate(Date date, boolean colon) {
    String formatted = dateFormate.format(date);
    return formatted.substring(0, 22) + (colon ? ":" : "") + formatted.substring(22);
  }

  /**
   * Transform Calendar to ISO 8601 string.
   *
   * @param calendar calendar
   * @return calendar format
   */
  public static String fromCalendar(Calendar calendar) {
    return fromCalendar(calendar, true);
  }

  /**
   * Transform Calendar to ISO 8601 string.<br>
   * When colon == true, return format, etc : "2018-11-15T09:36:27+08:00"<br>
   * When colon == false, return format, etc :"2018-11-15T09:36:27+0800"
   *
   * @param calendar calendar
   * @param colon true:zone append ":"，false: zone not append ":"
   * @return date string
   */
  public static String fromCalendar(Calendar calendar, boolean colon) {
    Date date = calendar.getTime();
    return fromDate(date, colon);
  }

  /**
   * Get current date and time formatted as ISO 8601 string.
   *
   * @return now date format string
   */
  public static String now() {
    return now(true);
  }

  /**
   * Get current date and time formatted as ISO 8601 string.
   *
   * @param colon true: zone append ":"，false: zone not append ":"
   * @return now date format string
   */
  public static String now(boolean colon) {
    return fromCalendar(Calendar.getInstance(), colon);
  }

  /**
   * Transform ISO 8601 string to Calendar.
   *
   * @param iso8601string iso8601string
   * @return Calendar
   * @throws ParseException ParseException
   */
  public static Calendar toCalendar(String iso8601string) throws ParseException {
    Calendar calendar = Calendar.getInstance();
    String s = iso8601string.replace("Z", "+00:00");

    if (hasColon(s)) {
      try {
        s = s.substring(0, 22) + s.substring(23); // to get rid of the
        // ":"
      } catch (IndexOutOfBoundsException e) {
        throw new ParseException("Invalid length", 0);
      }
    }

    Date date = dateFormate.parse(s);
    calendar.setTime(date);
    return calendar;
  }

  /**
   * Transform ISO 8601 string to Date.
   *
   * @param iso8601string iso8601string
   * @return to {@link Date} object
   * @throws ParseException ParseException
   */
  public static Date toDate(String iso8601string) throws ParseException {
    String s = iso8601string.replace("Z", "+00:00");

    try {
      if (hasColon(s)) {
        s = s.substring(0, 22) + s.substring(23); // to get rid of the
      } // ":"
    } catch (IndexOutOfBoundsException e) {
      throw new ParseException("Invalid length", 0);
    }

    return dateFormate.parse(s);
  }

  private static boolean hasColon(String iso8601string) {
    return iso8601string.charAt(22) == ':';
  }
}
