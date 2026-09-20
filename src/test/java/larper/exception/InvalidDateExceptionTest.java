package larper.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InvalidDateExceptionTest {
    @Test
    public void getMessage_deadlineDateError_includesExamples() {
        String message = new InvalidDateException("deadline").getMessage();

        assertTrue(message.contains("Missing lore. Larper needs the deadline date."));
        assertTrue(message.contains("2019-10-15"));
        assertTrue(message.contains("Aug 6"));
    }
}
