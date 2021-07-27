package ra.util;

import java.text.AttributedCharacterIterator;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Supplier;

/**
 * Thread safe version of {@link SimpleDateFormat}.
 *
 * @author Ray Li
 */
public class SimpleDateFormatThreadSafe {
  private ThreadLocal<SimpleDateFormat> localSimpleDateFormat;

  /** Thread safe. */
  public SimpleDateFormatThreadSafe() {
    super();
    localSimpleDateFormat =
        new ThreadLocal<SimpleDateFormat>() {
          @Override
          protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat();
          }
        };
  }

  /**
   * Thread safe.
   *
   * @param supplier Returns SimpleDateFormat
   */
  public SimpleDateFormatThreadSafe(Supplier<SimpleDateFormat> supplier) {
    localSimpleDateFormat =
        new ThreadLocal<SimpleDateFormat>() {
          @Override
          protected SimpleDateFormat initialValue() {
            return supplier.get();
          }
        };
  }

  /**
   * Thread safe.
   *
   * @param pattern pattern
   */
  public SimpleDateFormatThreadSafe(String pattern) {
    this(() -> new SimpleDateFormat(pattern));
  }

  /**
   * Thread safe.
   *
   * @param pattern time format pattern
   * @param timeZone time zone
   */
  public SimpleDateFormatThreadSafe(String pattern, TimeZone timeZone) {
    this(
        () -> {
          SimpleDateFormat o = new SimpleDateFormat(pattern);

          o.setTimeZone(timeZone);

          return o;
        });
  }

  /**
   * Thread safe.
   *
   * @param pattern time format pattern
   * @param formatSymbols formatSymbols
   */
  public SimpleDateFormatThreadSafe(String pattern, DateFormatSymbols formatSymbols) {
    localSimpleDateFormat =
        new ThreadLocal<SimpleDateFormat>() {
          @Override
          protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(pattern, formatSymbols);
          }
        };
  }

  /**
   * Thread safe.
   *
   * @param pattern time format pattern
   * @param locale locale
   */
  public SimpleDateFormatThreadSafe(String pattern, Locale locale) {
    localSimpleDateFormat =
        new ThreadLocal<SimpleDateFormat>() {
          @Override
          protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(pattern, locale);
          }
        };
  }

  /**
   * See {@link SimpleDateFormat#parseObject(String)}.
   *
   * @param source source
   * @return Object
   * @throws ParseException ParseException
   */
  public Object parseObject(String source) throws ParseException {
    return localSimpleDateFormat.get().parseObject(source);
  }

  /**
   * See {@link SimpleDateFormat#parseObject(String, ParsePosition)}.
   *
   * @param source source
   * @param pos pos
   * @return Object
   */
  public Object parseObject(String source, ParsePosition pos) {
    return localSimpleDateFormat.get().parseObject(source, pos);
  }

  /**
   * See {@link SimpleDateFormat#parse(String)}.
   *
   * @param source source
   * @return Date
   * @throws ParseException ParseException
   */
  public Date parse(String source) throws ParseException {
    return localSimpleDateFormat.get().parse(source);
  }

  /**
   * See {@link SimpleDateFormat#parseObject(String, ParsePosition)}.
   *
   * @param text text
   * @param pos pos
   * @return Date
   */
  public Date parse(String text, ParsePosition pos) {
    return localSimpleDateFormat.get().parse(text, pos);
  }

  /**
   * See {@link SimpleDateFormat#setCalendar(Calendar)}.
   *
   * @param newCalendar newCalendar
   */
  public void setCalendar(Calendar newCalendar) {
    localSimpleDateFormat.get().setCalendar(newCalendar);
  }

  /**
   * See {@link SimpleDateFormat#getCalendar()}.
   *
   * @return Calendar
   */
  public Calendar getCalendar() {
    return localSimpleDateFormat.get().getCalendar();
  }

  /**
   * See {@link SimpleDateFormat#getNumberFormat()}.
   *
   * @return NumberFormat
   */
  public NumberFormat getNumberFormat() {
    return localSimpleDateFormat.get().getNumberFormat();
  }

  /**
   * See {@link SimpleDateFormat#getTimeZone()}.
   *
   * @return TimeZone
   */
  public TimeZone getTimeZone() {
    return localSimpleDateFormat.get().getTimeZone();
  }

  /**
   * See {@link SimpleDateFormat#isLenient()}.
   *
   * @return boolean
   */
  public boolean isLenient() {
    return localSimpleDateFormat.get().isLenient();
  }

  /**
   * See {@link SimpleDateFormat#get2DigitYearStart()}.
   *
   * @return Date
   */
  public Date get2DigitYearStart() {
    return localSimpleDateFormat.get().get2DigitYearStart();
  }

  /**
   * See {@link SimpleDateFormat#format(Date, StringBuffer, FieldPosition)}.
   *
   * @param date date
   * @param toAppendTo toAppendTo
   * @param pos pos
   * @return StringBuffer
   */
  public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
    return localSimpleDateFormat.get().format(date, toAppendTo, pos);
  }

  /**
   * See {@link SimpleDateFormat#format(Date)}.
   *
   * @param date date
   * @return String
   */
  public String format(Date date) {
    return localSimpleDateFormat.get().format(date);
  }

  /**
   * See {@link SimpleDateFormat#format(Object)}.
   *
   * @param obj obj
   * @return String
   */
  public String format(Object obj) {
    return localSimpleDateFormat.get().format(obj);
  }

  /**
   * See {@link SimpleDateFormat#formatToCharacterIterator(Object)}.
   *
   * @param obj obj
   * @return AttributedCharacterIterator
   */
  public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
    return localSimpleDateFormat.get().formatToCharacterIterator(obj);
  }

  /**
   * See {@link SimpleDateFormat#toPattern()}.
   *
   * @return String
   */
  public String toPattern() {
    return localSimpleDateFormat.get().toPattern();
  }

  /**
   * See {@link SimpleDateFormat#toLocalizedPattern()}.
   *
   * @return String
   */
  public String toLocalizedPattern() {
    return localSimpleDateFormat.get().toLocalizedPattern();
  }

  /**
   * See {@link SimpleDateFormat#applyPattern(String)}.
   *
   * @param pattern pattern
   */
  public void applyPattern(String pattern) {
    localSimpleDateFormat.get().applyPattern(pattern);
  }

  /**
   * See {@link SimpleDateFormat#applyLocalizedPattern(String)}.
   *
   * @param pattern pattern
   */
  public void applyLocalizedPattern(String pattern) {
    localSimpleDateFormat.get().applyLocalizedPattern(pattern);
  }

  /**
   * See {@link SimpleDateFormat#getDateFormatSymbols()}.
   *
   * @return DateFormatSymbols
   */
  public DateFormatSymbols getDateFormatSymbols() {
    return localSimpleDateFormat.get().getDateFormatSymbols();
  }

  @Override
  public Object clone() {
    return localSimpleDateFormat.get().clone();
  }

  @Override
  public int hashCode() {
    return localSimpleDateFormat.get().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return localSimpleDateFormat.get().equals(obj);
  }

  @Override
  public String toString() {
    return localSimpleDateFormat.get().toString();
  }
}
