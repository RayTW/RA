package ra.ref;

/**
 * Pair reference.
 *
 * @author Ray Li
 * @param <L> Left class member type.
 * @param <R> Right class member type.
 */
public class BiReference<L, R> {
  private L left;
  private R right;

  /** Initialize. */
  public BiReference() {}

  /**
   * Initialize.
   *
   * @param left left
   * @param right right
   */
  public BiReference(L left, R right) {
    this.left = left;
    this.right = right;
  }

  /**
   * Returns element of left.
   *
   * @return element
   */
  public L getLeft() {
    return left;
  }

  /** Set element of the left. */
  public void setLeft(L left) {
    this.left = left;
  }

  /**
   * Returns the left whether is null.
   *
   * @return element is null.
   */
  public boolean isLeftNull() {
    return left == null;
  }

  /**
   * Returns the left whether not null.
   *
   * @return element not null.
   */
  public boolean isNotLeftNull() {
    return left != null;
  }

  /**
   * Returns element of right.
   *
   * @return element
   */
  public R getRight() {
    return right;
  }

  /** Set element of the right. */
  public void setRight(R right) {
    this.right = right;
  }

  /**
   * Returns the right whether is null.
   *
   * @return element is null.
   */
  public boolean isRightNull() {
    return right == null;
  }

  /**
   * Returns the left whether not null.
   *
   * @return element not null.
   */
  public boolean isNotRightNull() {
    return right != null;
  }

  @Override
  public String toString() {
    return "left = " + String.valueOf(left) + ",right = " + String.valueOf(right);
  }
}
