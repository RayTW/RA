package ra.net;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** Test class. */
public class TransmissionEndTest {

  @Test
  public void testGetCharNewLine() {
    assertEquals('\n', TransmissionEnd.NEW_LINE.getChar());
  }

  @Test
  public void testGetStringNewLine() {
    assertEquals("\n", TransmissionEnd.NEW_LINE.getString());
  }

  @Test
  public void testGetCharFromFeed() {
    assertEquals('\f', TransmissionEnd.FORM_FEED.getChar());
  }

  @Test
  public void testGetStringFromFeed() {
    assertEquals("\f", TransmissionEnd.FORM_FEED.getString());
  }
}
