package kojispawn.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests input validation for the JavaFX chat window.
 */
public class MainWindowTest {
    @Test
    public void isBlankInput_emptyOrWhitespaceOnlyInput_returnsTrue() {
        assertTrue(MainWindow.isBlankInput(""));
        assertTrue(MainWindow.isBlankInput("   \t"));
    }

    @Test
    public void isBlankInput_commandInput_returnsFalse() {
        assertFalse(MainWindow.isBlankInput("list"));
    }
}
