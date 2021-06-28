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

  public Object parseObject(String source) throws ParseException {
    return localSimpleDateFormat.get().parseObject(source);
  }

  public Object parseObject(String source, ParsePosition pos) {
    return localSimpleDateFormat.get().parseObject(source, pos);
  }

  @Override
  public String toString() {
    return localSimpleDateFormat.get().toString();
  }

  public Date parse(String source) throws ParseException {
    return localSimpleDateFormat.get().parse(source);
  }

  public Date parse(String text, ParsePosition pos) {
    return localSimpleDateFormat.get().parse(text, pos);
  }

  public void setCalendar(Calendar newCalendar) {
    localSimpleDateFormat.get().setCalendar(newCalendar);
  }

  public Calendar getCalendar() {
    return localSimpleDateFormat.get().getCalendar();
  }

  public NumberFormat getNumberFormat() {
    return localSimpleDateFormat.get().getNumberFormat();
  }

  public TimeZone getTimeZone() {
    return localSimpleDateFormat.get().getTimeZone();
  }

  public boolean isLenient() {
    return localSimpleDateFormat.get().isLenient();
  }

  public Date get2DigitYearStart() {
    return localSimpleDateFormat.get().get2DigitYearStart();
  }

  public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
    return localSimpleDateFormat.get().format(date, toAppendTo, pos);
  }

  public String format(Date date) {
    return localSimpleDateFormat.get().format(date);
  }

  public String format(Object obj) {
    return localSimpleDateFormat.get().format(obj);
  }

  public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
    return localSimpleDateFormat.get().formatToCharacterIterator(obj);
  }

  public String toPattern() {
    return localSimpleDateFormat.get().toPattern();
  }

  public String toLocalizedPattern() {
    return localSimpleDateFormat.get().toLocalizedPattern();
  }

  public void applyPattern(String pattern) {
    localSimpleDateFormat.get().applyPattern(pattern);
  }

  public void applyLocalizedPattern(String pattern) {
    localSimpleDateFormat.get().applyLocalizedPattern(pattern);
  }

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
}
