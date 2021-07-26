package ra.util.defensive;

/**
 * defined the reason about defensive the error which was known.
 *
 * @author Kevin Tsai
 */
public enum DefaultReason implements DefensiveReason {
  /** Required fields are missing. */
  REASON_NON_EXISTS(1, "Required fields are missing"),
  /** field value not number. */
  REASON_NOT_INT(2, "field value not number"),
  /** field value has space. */
  REASON_HAS_SPACE(3, "field value has space");

  private int value;
  private String detail;

  private DefaultReason(int value, String detail) {
    this.value = value;
    this.detail = detail;
  }

  @Override
  public int getValue() {
    return value;
  }

  @Override
  public String getDetail() {
    return detail;
  }
}
