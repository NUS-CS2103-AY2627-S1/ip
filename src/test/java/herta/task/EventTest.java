package herta.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests event range validation and calendar-date overlap behavior.
 */
class EventTest {

    @Test
    void fromStorage_validValues_reconstructEvent() {
        Event event = Event.fromStorage("team meeting", "2019-12-03T10:00:00",
                "2019-12-03T11:00:00");

        assertEquals("team meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 3, 10, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 3, 11, 0), event.getTo());
    }

    @Test
    void fromStorage_invalidRange_throwsHelpfulException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Event.fromStorage("team meeting", "2019-12-03T11:00:00",
                        "2019-12-03T10:00:00"));

        assertEquals("Invalid saved event: end must be after start.", exception.getMessage());
    }

    @Test
    void constructor_nonIncreasingRange_throwsIllegalArgumentException() {
        LocalDateTime start = LocalDateTime.of(2019, 10, 15, 10, 0);

        IllegalArgumentException sameTime = assertThrows(IllegalArgumentException.class, () ->
                new Event("meeting", start, start));
        IllegalArgumentException earlierEnd = assertThrows(IllegalArgumentException.class, () ->
                new Event("meeting", start, start.minusMinutes(1)));

        assertEquals("Event end must be after its start.", sameTime.getMessage());
        assertEquals("Event end must be after its start.", earlierEnd.getMessage());
    }

    @Test
    void occursOn_eventSpanningMidnight_matchesBothCalendarDates() {
        Event event = new Event("overnight event",
                LocalDateTime.of(2019, 10, 14, 23, 0),
                LocalDateTime.of(2019, 10, 15, 1, 0));

        assertTrue(event.occursOn(LocalDate.of(2019, 10, 14)));
        assertTrue(event.occursOn(LocalDate.of(2019, 10, 15)));
        assertFalse(event.occursOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    void isUpcoming_startInclusiveAndEndExclusive() {
        LocalDateTime start = LocalDateTime.of(2019, 10, 15, 10, 0);
        Event event = new Event("meeting", start, start.plusHours(1));

        assertTrue(event.isUpcoming(start, start.plusDays(1)));
        assertFalse(event.isUpcoming(start.plusHours(1), start.plusDays(1)));
    }

    @Test
    void eventFormatting_includesStatusDescriptionAndRange() {
        Event event = new Event("team meeting",
                LocalDateTime.of(2019, 12, 3, 10, 0),
                LocalDateTime.of(2019, 12, 3, 11, 0));

        assertEquals(LocalDateTime.of(2019, 12, 3, 10, 0),
                event.getScheduledDateTime().orElseThrow());
        assertEquals("E | 0 | team meeting | 2019-12-03T10:00:00 | 2019-12-03T11:00:00",
                event.toStorageString());
        assertEquals("[E][ ] team meeting (from: Dec 03 2019, 10:00 AM to: "
                + "Dec 03 2019, 11:00 AM)", event.toString());
    }

    @Test
    void eventConstructor_nullEndpoints_rejectInput() {
        LocalDateTime start = LocalDateTime.of(2019, 12, 3, 10, 0);
        LocalDateTime end = LocalDateTime.of(2019, 12, 3, 11, 0);

        assertThrows(NullPointerException.class, () -> new Event("event", null, end));
        assertThrows(NullPointerException.class, () -> new Event("event", start, null));
    }

    @Test
    void occursOn_exactDayBoundaries_excludesNonOverlappingDates() {
        Event event = new Event("event", LocalDateTime.of(2019, 12, 3, 10, 0),
                LocalDateTime.of(2019, 12, 3, 11, 0));

        assertTrue(event.occursOn(LocalDate.of(2019, 12, 3)));
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 4)));
        assertThrows(NullPointerException.class, () -> event.occursOn(null));
    }

    @Test
    void occursOn_midnightEndBoundary_excludesFollowingDate() {
        Event event = new Event("event", LocalDateTime.of(2019, 12, 3, 23, 0),
                LocalDateTime.of(2019, 12, 4, 0, 0));

        assertTrue(event.occursOn(LocalDate.of(2019, 12, 3)));
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 4)));
    }

    @Test
    void fromStorage_malformedEndpointsAndCompletionState_areHandled() {
        assertThrows(IllegalArgumentException.class, () ->
                Event.fromStorage("event", "not-a-date", "2019-12-03T11:00:00"));
        assertThrows(IllegalArgumentException.class, () ->
                Event.fromStorage("event", "2019-12-03T10:00:00", "not-a-date"));
        Event event = Event.fromStorage("event", "2019-12-03", "2019-12-04T00:00:00");

        event.markAsDone();

        assertEquals("E | 1 | event | 2019-12-03T00:00:00 | 2019-12-04T00:00:00",
                event.toStorageString());
    }

    @Test
    void event_supportedBoundaryYears_preserveStorageSerialization() {
        Event minimumEvent = new Event("minimum", LocalDateTime.of(1, 1, 1, 0, 0),
                LocalDateTime.of(1, 1, 1, 0, 1));
        Event maximumEvent = new Event("maximum", LocalDateTime.of(9999, 12, 31, 23, 58),
                LocalDateTime.of(9999, 12, 31, 23, 59));

        assertEquals("E | 0 | minimum | 0001-01-01T00:00:00 | 0001-01-01T00:01:00",
                minimumEvent.toStorageString());
        assertEquals("E | 0 | maximum | 9999-12-31T23:58:00 | 9999-12-31T23:59:00",
                maximumEvent.toStorageString());
    }

    @Test
    void occursOn_maximumDateWithEarlierEvent_returnsFalseWithoutOverflow() {
        Event event = new Event("earlier event", LocalDateTime.of(9999, 12, 30, 10, 0),
                LocalDateTime.of(9999, 12, 30, 11, 0));

        assertFalse(event.occursOn(LocalDate.MAX));
    }
}
