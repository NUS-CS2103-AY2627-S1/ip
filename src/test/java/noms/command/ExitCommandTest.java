package noms.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link ExitCommand#isExit()}, the one place in the command hierarchy
 * that overrides the {@code false} default to signal that the main loop should
 * stop. A regression here would either hang the loop or exit prematurely, so
 * the override is checked against a non-exit command for contrast.
 */
public class ExitCommandTest {

    @Test
    public void isExit_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    public void isExit_otherCommandsInheritFalse() {
        assertFalse(new ListCommand().isExit());
    }
}
