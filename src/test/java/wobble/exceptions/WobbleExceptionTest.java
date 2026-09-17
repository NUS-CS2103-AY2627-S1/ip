package wobble.exceptions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests whether Wobble errors request command suggestions when appropriate. */
public class WobbleExceptionTest {
    @Test
    public void defaultException_includesCommandSuggestion() {
        assertTrue(new WobbleException("invalid command").shouldSuggestCommand());
    }

    @Test
    public void rangeException_suppressesCommandSuggestion() {
        assertFalse(new WobbleException("invalid task number", false).shouldSuggestCommand());
    }
}
