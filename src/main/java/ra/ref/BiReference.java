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

  public BiReference() {}

  public BiReference(L left, R right) {
    this.left = left;
    this.right = right;
  }

  public L getLeft() {
    return left;
  }

  public void setLeft(L left) {
    this.left = left;
  }

  public boolean isLeftNull() {
    return left == null;
  }

  public boolean isNotLeftNull() {
    return left != null;
  }

  public R getRight() {
    return right;
  }

  public void setRight(R right) {
    this.right = right;
  }

  public boolean isRightNull() {
    return right == null;
  }

  public boolean isNotRightNull() {
    return right != null;
  }

  @Override
  public String toString() {
    return "left = " + String.valueOf(left) + ",right = " + String.valueOf(right);
  }
}
