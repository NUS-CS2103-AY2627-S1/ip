package exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for ListEmptyException.
 */
public class ListEmptyExceptionTest {
    @Test
    public void getMessage_returnsMessagePassedToConstructor() {
        ListEmptyException exception = new ListEmptyException("RIEEEEAAAU: HISTORY IS EMPTY");
        assertEquals("RIEEEEAAAU: HISTORY IS EMPTY", exception.getMessage());
    }

    @Test
    public void isRuntimeException() {
        assertTrue(new ListEmptyException("empty") instanceof RuntimeException);
    }
}
