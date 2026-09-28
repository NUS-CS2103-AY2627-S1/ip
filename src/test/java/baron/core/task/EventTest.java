package baron.core.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Event}.
 */
class EventTest {

    /**
     * Verifies the persistent and user-facing representations of an event task.
     */
    @Test
    void formattingDateRange_expectedFormatsReturned() {
        Event event = new Event(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "team meeting",
                LocalDateTime.of(2026, 8, 31, 10, 0),
                LocalDateTime.of(2026, 8, 31, 11, 30));

        assertEquals(
                "00000000-0000-0000-0000-000000000001 | E | 0 | team meeting | "
                        + " | 2026-08-31T10:00 | 2026-08-31T11:30",
                event.toFileString());
        assertEquals(
                """
                [E][ ] team meeting
                from: 10:00 AM, 31 Aug 2026
                to: 11:30 AM, 31 Aug 2026""",
                event.toString());
    }
}
