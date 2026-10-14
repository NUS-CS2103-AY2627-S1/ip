package bingus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests date matching for {@link Event} tasks.
 */
class EventTest {

    @Test
    void matchesDate_dateWithinMultipleDayEvent_returnsTrue() {
        Event event = new Event(
                "camp",
                LocalDateTime.of(2026, 8, 20, 9, 0),
                LocalDateTime.of(2026, 8, 22, 18, 0));
        assertTrue(event.matchesDate(LocalDate.of(2026, 8, 21)));
    }

    @Test
    void matchesDate_dateBeforeEvent_returnsFalse() {
        Event event = new Event(
                "camp",
                LocalDateTime.of(2026, 8, 20, 9, 0),
                LocalDateTime.of(2026, 8, 22, 18, 0));
        assertFalse(event.matchesDate(LocalDate.of(2026, 7, 21)));
    }

    @Test
    void matchesDate_startAndEndDate_returnsTrue() {
        Event event = new Event(
                "camp",
                LocalDateTime.of(2026, 8, 20, 9, 0),
                LocalDateTime.of(2026, 8, 22, 18, 0));

        assertTrue(event.matchesDate(LocalDate.of(2026, 8, 20)));
        assertTrue(event.matchesDate(LocalDate.of(2026, 8, 22)));
    }

    @Test
    void matchesDate_dateAfterEvent_returnsFalse() {
        Event event = new Event(
                "camp",
                LocalDateTime.of(2026, 8, 20, 9, 0),
                LocalDateTime.of(2026, 8, 22, 18, 0));

        assertFalse(event.matchesDate(LocalDate.of(2026, 8, 23)));
    }

    /**
     * Verifies that an event displays its type, completion status, and both endpoints.
     */
    @Test
    void getDisplayString_event_returnsFormattedDetails() {
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 20, 14, 0),
                LocalDateTime.of(2026, 9, 20, 16, 30));

        assertEquals("[E][○] project meeting (from: Sep 20 2026, 2:00 PM to: Sep 20 2026, 4:30 PM)",
                event.getDisplayString());
        event.mark();
        assertEquals("[E][✓] project meeting (from: Sep 20 2026, 2:00 PM to: Sep 20 2026, 4:30 PM)",
                event.getDisplayString());
    }
}
