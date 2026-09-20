package larper.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InvalidTagExceptionTest {
    @Test
    public void getMessage_invalidTag_includesOneWordExample() {
        InvalidTagException exception = new InvalidTagException();

        assertTrue(exception.getMessage().contains("one word"));
        assertTrue(exception.getMessage().contains("#school"));
    }
}
