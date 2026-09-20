package larper.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MissingTagExceptionTest {
    @Test
    public void getMessage_missingTag_includesTagPrompt() {
        MissingTagException exception = new MissingTagException();

        assertTrue(exception.getMessage().contains("tag name"));
        assertTrue(exception.getMessage().contains("#school"));
    }
}
