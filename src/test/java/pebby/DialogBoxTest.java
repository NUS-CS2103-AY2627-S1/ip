package pebby;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DialogBoxTest {

    @Test
    void isErrorMessage_invalidResponse_returnsTrue() {
        assertTrue(DialogBox.isErrorMessage("Invalid deadline: Please use a valid date."));
    }

    @Test
    void isErrorMessage_unknownResponse_returnsTrue() {
        assertTrue(DialogBox.isErrorMessage("Unknown command."));
    }

    @Test
    void isErrorMessage_successResponse_returnsFalse() {
        assertFalse(DialogBox.isErrorMessage("Got it. I've added this task."));
    }

    @Test
    void isErrorMessage_unknownCommandResponse_returnsFalse() {
        assertFalse(DialogBox.isErrorMessage("Hmmm... What does this mean?"));
    }
}
