package lumi.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests presentation decisions for graphical chat messages. */
public class DialogBoxTest {
    @Test
    public void isErrorMessage_errorAtStart_returnsTrue() {
        assertTrue(DialogBox.isErrorMessage(" Signal unclear: I don't recognize that command."));
    }

    @Test
    public void isErrorMessage_errorOnLaterLine_returnsTrue() {
        assertTrue(DialogBox.isErrorMessage("Task added.\n Signal unclear: I couldn't save the latest task changes."));
    }

    @Test
    public void isErrorMessage_successfulResponse_returnsFalse() {
        assertFalse(DialogBox.isErrorMessage("It's on the map. I've added this task."));
    }
}
