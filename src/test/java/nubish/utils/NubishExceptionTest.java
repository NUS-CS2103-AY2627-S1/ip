package nubish.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Tests Nubish-specific exception construction.
 */
public class NubishExceptionTest {
    /**
     * Verifies that the no-argument constructor creates an exception without a message.
     */
    @Test
    public void constructor_withoutMessage_hasNullMessage() {
        NubishException exception = new NubishException();

        assertNull(exception.getMessage());
    }

    /**
     * Verifies that the message constructor stores the given message.
     */
    @Test
    public void constructor_withMessage_hasMessage() {
        NubishException exception = new NubishException("error message");

        assertEquals("error message", exception.getMessage());
    }
}
