package luigibot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests the date-overlap behavior of {@link Event}.
 */
public class EventTest {

    @Test
    public void constructor_invalidStartDateTime_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                new Event("project meeting", "2026-02-30 1400", "2026-08-27 1600"));
    }

    @Test
    public void constructor_invalidEndDateTime_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                new Event("project meeting", "2026-08-27 1400", "2026-08-27 2500"));
    }

    @Test
    public void constructor_endEqualsStart_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () ->
                new Event("project meeting", "2026-08-27 1400", "2026-08-27 1400"));
    }

    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () ->
                new Event("project meeting", "2026-08-27 1600", "2026-08-27 1400"));
    }

    @Test
    public void toString_incompleteAndCompletedEvent_correctDisplayReturned() {
        Event event = new Event("project meeting", "2026-08-27 1400", "2026-08-27 1600");

        assertEquals("[E][ ] project meeting "
                + "(from: Aug 27 2026, 2:00 PM to: Aug 27 2026, 4:00 PM)", event.toString());

        event.mark();

        assertEquals("[E][X] project meeting "
                + "(from: Aug 27 2026, 2:00 PM to: Aug 27 2026, 4:00 PM)", event.toString());
    }

    @Test
    public void toFileString_incompleteAndCompletedEvent_correctStorageFormatReturned() {
        Event event = new Event("project meeting", "2026-08-27 1400", "2026-08-27 1600");

        assertEquals("E | 0 | project meeting | 2026-08-27 1400 | 2026-08-27 1600",
                event.toFileString());

        event.mark();

        assertEquals("E | 1 | project meeting | 2026-08-27 1400 | 2026-08-27 1600",
                event.toFileString());
    }

    @Test
    public void occursOn_eventWithinDate_trueReturned() {
        Event event = new Event("project meeting", "2026-08-27 1400", "2026-08-27 1600");

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 27)));
    }

    @Test
    public void occursOn_overnightEventStartDate_trueReturned() {
        Event event = new Event("night shift", "2026-08-27 2300", "2026-08-28 0100");

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 27)));
    }

    @Test
    public void occursOn_overnightEventEndDate_trueReturned() {
        Event event = new Event("night shift", "2026-08-27 2300", "2026-08-28 0100");

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 28)));
    }

    @Test
    public void occursOn_eventSpansWholeDate_trueReturned() {
        Event event = new Event("conference", "2026-08-26 2300", "2026-08-28 0100");

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 27)));
    }

    @Test
    public void occursOn_dateBeforeEvent_falseReturned() {
        Event event = new Event("project meeting", "2026-08-27 1400", "2026-08-27 1600");

        assertFalse(event.occursOn(LocalDate.of(2026, 8, 26)));
    }

    @Test
    public void occursOn_dateAfterEvent_falseReturned() {
        Event event = new Event("project meeting", "2026-08-27 1400", "2026-08-27 1600");

        assertFalse(event.occursOn(LocalDate.of(2026, 8, 28)));
    }

    @Test
    public void occursOn_eventEndsAtStartOfDate_falseReturned() {
        Event event = new Event("night shift", "2026-08-27 2300", "2026-08-28 0000");

        assertFalse(event.occursOn(LocalDate.of(2026, 8, 28)));
    }

    @Test
    public void overlaps_partiallyOverlappingEvents_trueReturned() {
        Event event = new Event("team meeting", "2026-08-27 1400", "2026-08-27 1600");
        Event overlappingEvent = new Event("project meeting", "2026-08-27 1500", "2026-08-27 1700");

        assertTrue(event.overlaps(overlappingEvent));
        assertTrue(overlappingEvent.overlaps(event));
    }

    @Test
    public void overlaps_eventContainedWithinAnotherEvent_trueReturned() {
        Event event = new Event("conference", "2026-08-27 0900", "2026-08-27 1700");
        Event containedEvent = new Event("lunch", "2026-08-27 1200", "2026-08-27 1300");

        assertTrue(event.overlaps(containedEvent));
    }

    @Test
    public void overlaps_eventStartsWhenOtherEnds_falseReturned() {
        Event event = new Event("team meeting", "2026-08-27 1400", "2026-08-27 1600");
        Event followingEvent = new Event("project meeting", "2026-08-27 1600", "2026-08-27 1700");

        assertFalse(event.overlaps(followingEvent));
    }
}
