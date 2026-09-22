package friday.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests errors caused by invalid Friday commands. */
class FridayExceptionTest {

    @Test
    void getMessage_exceptionWithMessage_returnsMessage() {
        FridayException exception = new FridayException("Invalid command");

        assertEquals("Invalid command", exception.getMessage());
    }
}
