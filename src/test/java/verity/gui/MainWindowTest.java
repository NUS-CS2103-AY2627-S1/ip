package verity.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests presentation logic used by the main JavaFX window.
 */
class MainWindowTest {

    @Test
    void formatResponseForGui_consoleSeparators_returnsCompactResponse() {
        String response = "____________________________________________________________\n"
                + "  A response with useful spacing.  \n"
                + "____________________________________________________________\n";

        String formattedResponse = MainWindow.formatResponseForGui(response);

        assertEquals("A response with useful spacing.", formattedResponse);
    }

    @Test
    void isErrorResponse_missingCommandType_returnsTrue() {
        assertTrue(MainWindow.isErrorResponse(null));
    }

    @Test
    void isErrorResponse_successfulCommandType_returnsFalse() {
        assertFalse(MainWindow.isErrorResponse("ListCommand"));
    }

    @Test
    void isHelpCommand_onlyHelpCommand_returnsTrue() {
        assertTrue(MainWindow.isHelpCommand("HelpCommand"));
        assertFalse(MainWindow.isHelpCommand("ListCommand"));
        assertFalse(MainWindow.isHelpCommand(null));
    }
}
