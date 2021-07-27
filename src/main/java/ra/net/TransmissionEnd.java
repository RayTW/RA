package ra.net;

/**
 * End of transmission.
 *
 * @author Ray Li
 */
public class TransmissionEnd {
  /** Form feed character. */
  public static final TransmissionEnd FORM_FEED = new TransmissionEnd('\f');

  /** New line character. */
  public static final TransmissionEnd NEW_LINE = new TransmissionEnd('\n');

  /** Bytes zero. */
  public static final byte[] BYTES_ZERO = new byte[0];

  private final char finishChar;
  private final byte finishByte;
  private final String string;

  /**
   * Initialize.
   *
   * @param c character
   */
  TransmissionEnd(char c) {
    finishChar = c;
    finishByte = (byte) c;
    string = String.valueOf(c);
  }

  /**
   * Returns the character.
   *
   * @return char
   */
  public char getChar() {
    return finishChar;
  }

  /**
   * Returns the character convert to byte.
   *
   * @return byte
   */
  public byte getByte() {
    return finishByte;
  }

  /**
   * Returns the character convert to string.
   *
   * @return string
   */
  public String getString() {
    return string;
  }

  /**
   * Returns text that append FORM_FEED and NEW_LINE.
   *
   * @param text text
   * @return text
   */
  public static String appendFeedNewLine(String text) {
    return text.concat(FORM_FEED.string).concat(NEW_LINE.string);
  }

  @Override
  public String toString() {
    return string;
  }
}
