package piplupbot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link PiplupBotException}, which carries its explanation as the
 * separate lines the bot replies with.
 *
 * <p>Those lines are what the user sees, and nearly every other test class
 * checks them. What only a developer sees -- in a stack trace, a debugger, or
 * the report of a failed test -- is {@link PiplupBotException#getMessage}, and
 * that is what this class checks.</p>
 */
public class PiplupBotExceptionTest {

    /**
     * The lines are joined with a space, so a message that has to fit on one
     * line still reads as the sentences the user would see.
     */
    @Test
    public void getMessage_twoLines_joinsThemWithASpace() {
        PiplupBotException exception =
                new PiplupBotException("Pip... There is no task numbered 5.", "Try: list.");

        assertEquals("Pip... There is no task numbered 5. Try: list.", exception.getMessage());
    }
}
