package jarvis.classes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the behavior of {@link Event}. */
public class EventTest {
    /** Verifies that an event formats date-only start and end values correctly. */
    @Test
    public void constructor_dateOnlyEventFormatsCorrectly() {
        Event event = new Event("Read book",
                LocalDateTime.of(2026, 8, 22, 0, 0),
                LocalDateTime.of(2026, 8, 23, 0, 0));

        assertEquals(Task.CompletionStatus.UNDONE, event.getStatus());
        assertEquals("[E][] Read book (from: 08 22 2026 to: 08 23 2026)", event.toString());
    }

    /** Verifies that a date only event can be marked as done and displays the done marker. */
    @Test
    public void setCompletionStatus_doneDateOnlyEventDisplaysDoneMarker() {
        Event event = new Event("Read book",
                LocalDateTime.of(2026, 8, 22, 0, 0),
                LocalDateTime.of(2026, 8, 23, 0, 0));

        event.setCompletionStatus(Task.CompletionStatus.DONE);

        assertEquals(Task.CompletionStatus.DONE, event.getStatus());
        assertEquals("[E][X] Read book (from: 08 22 2026 to: 08 23 2026)", event.toString());
    }

    /** Verifies that a completed date only event can be marked undone again. */
    @Test
    public void setCompletionStatus_undoneDateOnlyEventDisplaysUndoneMarker() {
        Event event = new Event("Read book",
                LocalDateTime.of(2026, 8, 22, 0, 0),
                LocalDateTime.of(2026, 8, 23, 0, 0));

        event.setCompletionStatus(Task.CompletionStatus.DONE);
        event.setCompletionStatus(Task.CompletionStatus.UNDONE);

        assertEquals(Task.CompletionStatus.UNDONE, event.getStatus());
        assertEquals("[E][] Read book (from: 08 22 2026 to: 08 23 2026)", event.toString());
    }

    /** Verifies that an event formats start and end date-time values correctly. */
    @Test
    public void constructor_eventWithTimeFormatsCorrectly() {
        Event event = new Event("Read book",
                LocalDateTime.of(2026, 8, 22, 9, 30),
                LocalDateTime.of(2026, 8, 22, 10, 30));

        assertEquals("[E][] Read book (from: 08 22 2026 09:30 to: 08 22 2026 10:30)",
                event.toString());
    }

    /** Verifies that a date and time event can be marked as done and displays the done marker. */
    @Test
    public void setCompletionStatus_doneDateAndTimeEventDisplaysDoneMarker() {
        Event event = new Event("Read book",
                LocalDateTime.of(2026, 8, 22, 9, 30),
                LocalDateTime.of(2026, 8, 23, 12, 30));

        event.setCompletionStatus(Task.CompletionStatus.DONE);

        assertEquals(Task.CompletionStatus.DONE, event.getStatus());
        assertEquals("[E][X] Read book (from: 08 22 2026 09:30 to: 08 23 2026 12:30)", event.toString());
    }

    /** Verifies that a completed date and time event can be marked undone again. */
    @Test
    public void setCompletionStatus_undoneDateAndTimeEventDisplaysUndoneMarker() {
        Event event = new Event("Read book",
                LocalDateTime.of(2026, 8, 22, 17, 45),
                LocalDateTime.of(2026, 8, 23, 20, 50));

        event.setCompletionStatus(Task.CompletionStatus.DONE);
        event.setCompletionStatus(Task.CompletionStatus.UNDONE);

        assertEquals(Task.CompletionStatus.UNDONE, event.getStatus());
        assertEquals("[E][] Read book (from: 08 22 2026 17:45 to: 08 23 2026 20:50)", event.toString());
    }


    /**
     * Tests that two events that partially overlap are considered
     * to be clashing.
     *
     * Example:
     * Event 1: 10:00 - 12:00
     * Event 2: 11:00 - 13:00
     */
    @Test
    public void clashesWith_partialOverlap_returnsTrue() {
        Event event1 = new Event(
                "Event 1",
                LocalDateTime.of(2026, 9, 12, 10, 0),
                LocalDateTime.of(2026, 9, 12, 12, 0)
        );

        Event event2 = new Event(
                "Event 2",
                LocalDateTime.of(2026, 9, 12, 11, 0),
                LocalDateTime.of(2026, 9, 12, 13, 0)
        );

        assertTrue(event1.clashesWith(event2));
    }

    /**
     * Tests that two events that partially overlap from the other
     * direction are considered to be clashing.
     *
     * Example:
     * Event 1: 11:00 - 13:00
     * Event 2: 10:00 - 12:00
     */
    @Test
    public void clashesWith_reversePartialOverlap_returnsTrue() {
        Event event1 = new Event(
                "Event 1",
                LocalDateTime.of(2026, 9, 12, 11, 0),
                LocalDateTime.of(2026, 9, 12, 13, 0)
        );

        Event event2 = new Event(
                "Event 2",
                LocalDateTime.of(2026, 9, 12, 10, 0),
                LocalDateTime.of(2026, 9, 12, 12, 0)
        );

        assertTrue(event1.clashesWith(event2));
    }

    /**
     * Tests that an event completely inside another event is
     * considered to be clashing.
     *
     * Example:
     * Event 1: 10:00 - 14:00
     * Event 2: 11:00 - 12:00
     */
    @Test
    public void clashesWith_eventInsideAnother_returnsTrue() {
        Event event1 = new Event(
                "Event 1",
                LocalDateTime.of(2026, 9, 12, 10, 0),
                LocalDateTime.of(2026, 9, 12, 14, 0)
        );

        Event event2 = new Event(
                "Event 2",
                LocalDateTime.of(2026, 9, 12, 11, 0),
                LocalDateTime.of(2026, 9, 12, 12, 0)
        );

        assertTrue(event1.clashesWith(event2));
    }

    /**
     * Tests that an event completely containing another event is
     * considered to be clashing.
     *
     * Example:
     * Event 1: 11:00 - 12:00
     * Event 2: 10:00 - 14:00
     */
    @Test
    public void clashesWith_eventContainsAnother_returnsTrue() {
        Event event1 = new Event(
                "Event 1",
                LocalDateTime.of(2026, 9, 12, 11, 0),
                LocalDateTime.of(2026, 9, 12, 12, 0)
        );

        Event event2 = new Event(
                "Event 2",
                LocalDateTime.of(2026, 9, 12, 10, 0),
                LocalDateTime.of(2026, 9, 12, 14, 0)
        );

        assertTrue(event1.clashesWith(event2));
    }

    /**
     * Tests that two events with exactly the same start and end times
     * are considered to be clashing.
     */
    @Test
    public void clashesWith_sameTime_returnsTrue() {
        Event event1 = new Event(
                "Event 1",
                LocalDateTime.of(2026, 9, 12, 10, 0),
                LocalDateTime.of(2026, 9, 12, 12, 0)
        );

        Event event2 = new Event(
                "Event 2",
                LocalDateTime.of(2026, 9, 12, 10, 0),
                LocalDateTime.of(2026, 9, 12, 12, 0)
        );

        assertTrue(event1.clashesWith(event2));
    }

    /**
     * Tests that two events immediately next to each other without
     * overlapping are not considered to be clashing.
     *
     * Example:
     * Event 1: 10:00 - 12:00
     * Event 2: 12:00 - 14:00
     */
    @Test
    public void clashesWith_touchingAtEnd_returnsFalse() {
        Event event1 = new Event(
                "Event 1",
                LocalDateTime.of(2026, 9, 12, 10, 0),
                LocalDateTime.of(2026, 9, 12, 12, 0)
        );

        Event event2 = new Event(
                "Event 2",
                LocalDateTime.of(2026, 9, 12, 12, 0),
                LocalDateTime.of(2026, 9, 12, 14, 0)
        );

        assertFalse(event1.clashesWith(event2));
    }

    /**
     * Tests that two events immediately next to each other without
     * overlapping are not considered to be clashing in the reverse
     * direction.
     *
     * Example:
     * Event 1: 12:00 - 14:00
     * Event 2: 10:00 - 12:00
     */
    @Test
    public void clashesWith_touchingAtStart_returnsFalse() {
        Event event1 = new Event(
                "Event 1",
                LocalDateTime.of(2026, 9, 12, 12, 0),
                LocalDateTime.of(2026, 9, 12, 14, 0)
        );

        Event event2 = new Event(
                "Event 2",
                LocalDateTime.of(2026, 9, 12, 10, 0),
                LocalDateTime.of(2026, 9, 12, 12, 0)
        );

        assertFalse(event1.clashesWith(event2));
    }

    /**
     * Tests that two events with a gap between them are not considered
     * to be clashing.
     *
     * Example:
     * Event 1: 10:00 - 12:00
     * Event 2: 13:00 - 15:00
     */
    @Test
    public void clashesWith_noOverlap_returnsFalse() {
        Event event1 = new Event(
                "Event 1",
                LocalDateTime.of(2026, 9, 12, 10, 0),
                LocalDateTime.of(2026, 9, 12, 12, 0)
        );

        Event event2 = new Event(
                "Event 2",
                LocalDateTime.of(2026, 9, 12, 13, 0),
                LocalDateTime.of(2026, 9, 12, 15, 0)
        );

        assertFalse(event1.clashesWith(event2));
    }

    /**
     * Tests that events on different dates do not clash even when
     * they have the same time of day.
     */
    @Test
    public void clashesWith_differentDates_returnsFalse() {
        Event event1 = new Event(
                "Event 1",
                LocalDateTime.of(2026, 9, 12, 10, 0),
                LocalDateTime.of(2026, 9, 12, 12, 0)
        );

        Event event2 = new Event(
                "Event 2",
                LocalDateTime.of(2026, 9, 13, 10, 0),
                LocalDateTime.of(2026, 9, 13, 12, 0)
        );

        assertFalse(event1.clashesWith(event2));
    }

}
