package penny.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit test suite for {@link PennyException}.
 * Verifies domain exception message retention.
 */
public class PennyExceptionTest {

    @Test
    public void constructor_withMessage_retainsMessage() {
        PennyException exception = new PennyException("Task not found");
        assertEquals("Task not found", exception.getMessage());
    }
}
