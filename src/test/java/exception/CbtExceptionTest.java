package exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests preservation of user-facing validation messages. */
public class CbtExceptionTest {
    /** Verifies that the exception preserves its user-facing message. */
    @Test
    public void constructor_messageProvided_messageReturned() {
        CbtException exception = new CbtException("Invalid command");

        assertEquals("Invalid command", exception.getMessage());
    }
}
