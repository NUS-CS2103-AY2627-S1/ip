package larper.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InvalidTimeExceptionTest {
    @Test
    public void getMessage_eventStartTimeError_includesOptionalTimePrompt() {
        String message = new InvalidTimeException("event start").getMessage();

        assertTrue(message.contains("Larper found the event start date, but the time is still allegedly missing."));
        assertTrue(message.contains("Time is optional"));
        assertTrue(message.contains("no time"));
    }
}
