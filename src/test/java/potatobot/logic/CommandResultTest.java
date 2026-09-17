package potatobot.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Checks that response factories distinguish replies, errors and exit requests.
 */
public class CommandResultTest {
    @Test
    public void reply_errorLikeText_remainsSuccessful() {
        CommandResult result = CommandResult.reply("Me no gets?");
        assertEquals("Me no gets?", result.message());
        assertFalse(result.isError());
        assertFalse(result.isExit());
    }

    @Test
    public void error_failureMessage_keepsSessionOpen() {
        CommandResult result = CommandResult.error("Invalid date");
        assertEquals("Invalid date", result.message());
        assertTrue(result.isError());
        assertFalse(result.isExit());
    }

    @Test
    public void exit_finalMessage_requestsSuccessfulExit() {
        CommandResult result = CommandResult.exit("Bye");
        assertEquals("Bye", result.message());
        assertTrue(result.isExit());
        assertFalse(result.isError());
    }
}
