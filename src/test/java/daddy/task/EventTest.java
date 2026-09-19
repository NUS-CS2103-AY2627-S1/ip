package daddy.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the time-range invariant of event tasks.
 */
class EventTest {
    /** Verifies that an event accepts an end time strictly after its start time. */
    @Test
    void constructor_endAfterStart_createsEvent() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 14, 14, 0);

        assertDoesNotThrow(() -> new Event("meeting", start, start.plusHours(2)));
    }

    /** Verifies that zero-length and reversed event ranges are rejected. */
    @Test
    void constructor_endNotAfterStart_exceptionThrown() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 14, 14, 0);
        LocalDateTime reversedEnd = start.minusHours(1);

        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", start, start));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", start, reversedEnd));
    }
}
