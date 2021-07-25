package ra.server.basis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test class.
 *
 * @author Ray Li
 */
public class CommandsVerificationTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    CommandsVerification.loadCommands("./unittest/commands.json");
  }

  @After
  public void tearDown() throws Exception {
    CommandsVerification.clear();
  }

  @Test
  public void testLoadCommandsSize() throws IOException {
    int size = CommandsVerification.getCommandsAuthorizationSize();

    assertEquals(3, size);
  }

  @Test
  public void testLoadCommandsStream() throws IOException {
    String actual =
        CommandsVerification.getCommandsAuthorizationStream()
            .map(element -> element.getKey())
            .filter(element -> "/v1/server/echo".equals(element))
            .findFirst()
            .get();

    assertEquals("/v1/server/echo", actual);
  }

  @Test
  public void testLoadAccessTokensSize() throws IOException {
    int size = CommandsVerification.getAccessTokensSize();
    assertEquals(2, size);
  }

  @Test
  public void testLoadAccessTokensStream() throws IOException {
    String actual =
        CommandsVerification.getAccessTokensStream()
            .map(element -> element.getKey())
            .filter(element -> "aabbcc".equals(element))
            .findFirst()
            .get();

    assertEquals("aabbcc", actual);
  }

  @Test
  public void testClear() throws IOException {
    CommandsVerification.clear();

    assertEquals(0, CommandsVerification.getAccessTokensSize());
    assertEquals(0, CommandsVerification.getCommandsAuthorizationSize());
  }

  @Test
  public void testIsValidAuthVerifySuccess() throws IOException {
    assertTrue(CommandsVerification.isValidAuth("/v1/server/echo", "aabbcc"));
  }

  @Test
  public void testIsValidAuthF5() throws IOException {
    assertTrue(
        CommandsVerification.isValidAuth(
            CommandsVerification.HEARTBEAT, CommandsVerification.AUTHORIZATION_F5));
  }

  @Test
  public void testIsValidAuthMonitor() throws IOException {
    assertTrue(
        CommandsVerification.isValidAuth(
            CommandsVerification.MONITOR, CommandsVerification.AUTHORIZATION_MONITOR));
  }

  @Test
  public void testIsValidAuthF5CommandNotFound() throws IOException {
    exceptionRule.expect(CommandNotFoundException.class);
    CommandsVerification.isValidAuth("xxx", CommandsVerification.AUTHORIZATION_F5);
  }

  @Test
  public void testIsValidAuthMonitorommandNotFound() throws IOException {
    exceptionRule.expect(CommandNotFoundException.class);
    CommandsVerification.isValidAuth("xxx", CommandsVerification.AUTHORIZATION_MONITOR);
  }

  @Test
  public void testIsValidAuthAuthUseNull() throws IOException {
    assertFalse(CommandsVerification.isValidAuth("/v1/server/echo", null));
  }

  @Test
  public void testIsValidAuthCommandUseNull() throws IOException {
    exceptionRule.expect(CommandNotFoundException.class);
    assertTrue(CommandsVerification.isValidAuth(null, "aabbcc"));
  }

  @Test
  public void testIsValidAuthCommandUseNoExistsAuth() throws IOException {
    assertFalse(CommandsVerification.isValidAuth("/v1/server/echo", "ccc"));
  }

  @Test
  public void testIsValidAuthCommandUseNoExistsCommand() throws IOException {
    exceptionRule.expect(CommandNotFoundException.class);
    assertFalse(CommandsVerification.isValidAuth("/server/test", "aabbcc"));
  }
}
