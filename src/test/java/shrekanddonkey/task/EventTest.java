package shrekanddonkey.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests task operations and string representations of {@link Event}.
 */
public class EventTest {

    @Test
    public void toString_validEvent_formattedWithFromAndTo() {
        LocalDateTime from = LocalDateTime.of(2026, 11, 1, 10, 0);
        LocalDateTime to = LocalDateTime.of(2026, 11, 1, 12, 0);
        Event event = new Event("project meeting", from, to);
        assertEquals("[E][ ] project meeting (from: Nov 01 2026, 10:00 AM to: Nov 01 2026, 12:00 PM)",
                event.toString());
    }

    @Test
    public void toString_completedEvent_formattedWithDoneMarker() {
        LocalDateTime from = LocalDateTime.of(2026, 11, 1, 10, 0);
        LocalDateTime to = LocalDateTime.of(2026, 11, 1, 12, 0);
        Event event = new Event("project meeting", from, to);
        event.markAsDone();
        assertEquals("[E][X] project meeting (from: Nov 01 2026, 10:00 AM to: Nov 01 2026, 12:00 PM)",
                event.toString());
    }
}
