package oz.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link OzException}.
 */
public class OzExceptionTest {

    @Test
    public void constructor_validMessage_preservesMessage() {
        String message = "Something went wrong in the contraption.";
        OzException exception = new OzException(message);
        assertEquals(message, exception.getMessage());
    }
}
