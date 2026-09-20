package larper.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NoTaskTypeExceptionTest {
    @Test
    public void getMessage_missingTaskType_includesPersonalisedHelp() {
        String message = new NoTaskTypeException().getMessage();

        assertTrue(message.contains("Source: you made that command up."));
        assertTrue(message.contains("todo DESCRIPTION"));
    }
}
