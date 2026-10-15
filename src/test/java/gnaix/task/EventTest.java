package gnaix.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void occursOn_dateWithinEventRange_trueReturned() {
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 1, 14, 0),
                LocalDateTime.of(2026, 9, 3, 16, 0));

        assertTrue(event.occursOn(LocalDate.of(2026, 9, 2)));
    }

    @Test
    void occursOn_startDate_trueReturned() {
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 1, 14, 0),
                LocalDateTime.of(2026, 9, 3, 16, 0));

        assertTrue(event.occursOn(LocalDate.of(2026, 9, 1)));
    }

    @Test
    void occursOn_endDate_trueReturned() {
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 1, 14, 0),
                LocalDateTime.of(2026, 9, 3, 16, 0));

        assertTrue(event.occursOn(LocalDate.of(2026, 9, 3)));
    }

    @Test
    void occursOn_dateOutsideEventRange_falseReturned() {
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 1, 14, 0),
                LocalDateTime.of(2026, 9, 3, 16, 0));

        assertFalse(event.occursOn(LocalDate.of(2026, 9, 4)));
    }

    @Test
    void occursOn_dateBeforeEventRange_falseReturned() {
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 1, 14, 0),
                LocalDateTime.of(2026, 9, 3, 16, 0));

        assertFalse(event.occursOn(LocalDate.of(2026, 8, 31)));
    }

    @Test
    void toString_eventTask_timesDisplayed() {
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 1, 14, 0),
                LocalDateTime.of(2026, 9, 1, 16, 0));

        assertEquals(
                "[E][ ] project meeting (from: Sep 01 2026 14:00 to: Sep 01 2026 16:00)",
                event.toString());
    }

    @Test
    void toString_taggedCompletedEvent_statusTimesAndTagsDisplayed() {
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 1, 14, 0),
                LocalDateTime.of(2026, 9, 1, 16, 0));
        event.addTag("#school");
        event.markAsComplete();

        assertEquals(
                "[E][X] project meeting #school (from: Sep 01 2026 14:00 to: Sep 01 2026 16:00)",
                event.toString());
    }
}
