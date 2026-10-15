package snoopy.gui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests response classification used to select GUI dialog styles.
 */
public class MainWindowTest {

    @Test
    public void isErrorResponse_errorWithLeadingWhitespace_returnsTrue() {
        assertTrue(MainWindow.isErrorResponse(" OOPS! Please enter a command."));
    }

    @Test
    public void isErrorResponse_normalReply_returnsFalse() {
        assertFalse(MainWindow.isErrorResponse(" Here are the tasks in your list:"));
    }
}
