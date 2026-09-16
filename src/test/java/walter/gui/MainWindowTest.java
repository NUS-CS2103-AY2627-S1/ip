package walter.gui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests GUI input filtering without requiring a live JavaFX window.
 */
public class MainWindowTest {
    @Test
    public void isBlankInput_emptyAndWhitespaceOnly_ignored() {
        assertTrue(MainWindow.isBlankInput(null));
        assertTrue(MainWindow.isBlankInput(""));
        assertTrue(MainWindow.isBlankInput("  \t\n"));
        assertFalse(MainWindow.isBlankInput("todo read book"));
    }
}
