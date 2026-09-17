package crystal.gui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests command detection used by the JavaFX window lifecycle.
 */
public class MainWindowTest {
    /** Verifies that only the exact exit command schedules the window to close. */
    @Test
    public void isExitCommand_exactAndNonExactCommands_returnsExpectedResult() {
        assertTrue(MainWindow.isExitCommand("bye"));
        assertFalse(MainWindow.isExitCommand("bye now"));
        assertFalse(MainWindow.isExitCommand("BYE"));
    }

    /** Verifies that only Crystal exception responses receive error styling. */
    @Test
    public void isErrorResponse_errorAndOrdinaryResponses_returnsExpectedResult() {
        assertTrue(MainWindow.isErrorResponse(
                "Crystal: Oopsies!!! That task number does not exist!"));
        assertFalse(MainWindow.isErrorResponse(
                "Crystal: Your task list is all clear, well done!"));
    }
}
