package james.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link UserInputException}.
 */
public class UserInputExceptionTest {

    /**
     * Tests that the constructor correctly stores and preserves the error message.
     */
    @Test
    public void constructor_validMessage_preservesMessage() {
        String errorMessage = "Custom error message";
        UserInputException exception = new UserInputException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }
}
