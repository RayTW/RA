package ra.net;

/**
 * End of transmission.
 *
 * @author Ray Li
 */
public class TransmissionEnd {
  public static final TransmissionEnd FORM_FEED = new TransmissionEnd('\f');
  public static final TransmissionEnd NEW_LINE = new TransmissionEnd('\n');
  public static final byte[] BYTES_ZERO = new byte[0];

  private final char finishChar;
  private final byte finishByte;
  private final String string;

  TransmissionEnd(char c) {
    finishChar = c;
    finishByte = (byte) c;
    string = String.valueOf(c);
  }

  public char getChar() {
    return finishChar;
  }

  public byte getByte() {
    return finishByte;
  }

  public String getString() {
    return string;
  }

  public static String appendFeedNewLine(String text) {
    return text.concat(FORM_FEED.string).concat(NEW_LINE.string);
  }

  public String toString() {
    return string;
  }
}
