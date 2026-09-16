package puyo.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import puyo.PuyoException;

public class EventTest {

    @Test
    public void constructor_validTimes_success() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 15, 12, 0);

        // Should not throw any exception for valid start before end
        assertDoesNotThrow(() -> new Event("Valid Event", start, end));
    }

    @Test
    public void constructor_startAfterEnd_exceptionThrown() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 15, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 15, 12, 0);

        // Should throw PuyoException when start time is after end time
        assertThrows(PuyoException.class, () -> new Event("Invalid Event", start, end));
    }

    @Test
    public void constructor_equalTimes_exceptionThrown() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 15, 10, 0);

        // Should throw PuyoException when start time equals end time
        assertThrows(PuyoException.class, () -> new Event("Invalid Event", start, end));
    }
}
