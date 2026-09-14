package penguin.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

/**
 * Tests the checked exception used for user-facing failures.
 */
class PenguinExceptionTest {
    @Test
    void penguinException_message_preservesOriginalMessage() {
        PenguinException exception = new PenguinException("Invalid input.");

        assertEquals("Invalid input.", exception.getMessage());
    }

    @Test
    void penguinException_type_isCheckedException() {
        assertInstanceOf(Exception.class,
                new PenguinException("Invalid input."));
    }
}
