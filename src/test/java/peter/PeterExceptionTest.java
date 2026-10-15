package peter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PeterException}.
 */
public class PeterExceptionTest {
    @Test
    public void getMessage_returnsConstructorMessage() {
        PeterException exception = new PeterException("that's not a number my guy.");
        assertEquals("that's not a number my guy.", exception.getMessage());
    }
}
