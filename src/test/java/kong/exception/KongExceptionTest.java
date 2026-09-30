package kong.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class KongExceptionTest {
    @Test
    void constructor_preservesErrorMessage() {
        KongException exception = new KongException("Test error message");

        assertEquals("Test error message", exception.getMessage());
    }
}
