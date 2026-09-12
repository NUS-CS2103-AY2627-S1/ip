package bob.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/** Tests user-facing exception message and cause preservation. */
class BobExceptionTest {

    @Test
    void messageOnlyConstructor_preservesMessage() {
        BobException exception = new BobException("Invalid command");

        assertEquals("Invalid command", exception.getMessage());
    }

    @Test
    void messageAndCauseConstructor_preservesMessageAndCause() {
        IllegalArgumentException cause = new IllegalArgumentException("bad argument");
        BobException exception = new BobException("Invalid command", cause);

        assertEquals("Invalid command", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
