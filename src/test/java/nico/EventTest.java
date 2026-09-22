package nico;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EventTest {
    @Test
    void getStartTime_newEvent_startTimeReturned() {
        LocalDateTime startTime = LocalDateTime.of(2026, 8, 25, 14, 0);
        Event event = new Event("project meeting", startTime, LocalDateTime.of(2026, 8, 25, 16, 0));

        assertEquals(startTime, event.getStartTime());
    }

    @Test
    void toString_newEvent_formattedAsIncompleteEvent() {
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2026, 8, 25, 14, 0),
                LocalDateTime.of(2026, 8, 25, 16, 0));

        assertEquals("[E][ ] project meeting (from: Aug 25 2026 14:00 to: Aug 25 2026 16:00)", event.toString());
    }

    @Test
    void toString_completedEvent_formattedAsCompletedEvent() {
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2026, 8, 25, 14, 0),
                LocalDateTime.of(2026, 8, 25, 16, 0));
        event.markAsDone();

        assertEquals("[E][X] project meeting (from: Aug 25 2026 14:00 to: Aug 25 2026 16:00)", event.toString());
    }
}
