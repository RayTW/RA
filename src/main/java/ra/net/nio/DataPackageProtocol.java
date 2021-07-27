package ra.net.nio;

/**
 * Transmission package protocol.
 *
 * @author Ray Li
 */
public interface DataPackageProtocol {
  /** Maximum Transmission Unit. */
  public static final int MTU = 65535;

  /** End package. */
  public static final byte END_PACKAGE = 0x00;

  /** Package. */
  public static final byte PACKAGE = 0x01;

  /** Package header length. */
  public static final int HEADER_LENGTH = 5;
}
