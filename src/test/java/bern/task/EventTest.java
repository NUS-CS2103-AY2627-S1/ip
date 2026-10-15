package bern.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import bern.datetime.DateTimeFactory;

/**
 * Verifies event date membership at timed, all-day, and legacy invalid boundaries.
 */
public class EventTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 15);

    @Test
    public void isOnDay_midnightEnd_excludesEndDate() throws ParseException {
        Event event = makeEvent("15/9/2026 @ 23:00", "16/9/2026 @ 00:00");

        assertTrue(event.isOnDay(DateTimeFactory.parseDateTime("15/9/2026")));
        assertFalse(event.isOnDay(DateTimeFactory.parseDateTime("16/9/2026")));
        assertEquals(DATE, event.getLastOccupiedDate());
    }

    @Test
    public void isOnDay_allDayEvent_includesBothBoundaryDates() throws ParseException {
        Event event = makeEvent("15/9/2026", "16/9/2026");

        assertFalse(event.isOnDay(DateTimeFactory.parseDateTime("14/9/2026")));
        assertTrue(event.isOnDay(DateTimeFactory.parseDateTime("15/9/2026")));
        assertTrue(event.isOnDay(DateTimeFactory.parseDateTime("16/9/2026")));
        assertFalse(event.isOnDay(DateTimeFactory.parseDateTime("17/9/2026")));
        assertTrue(event.isAllDay());
        assertTrue(event.hasPositiveDuration());
    }

    @Test
    public void isOnDay_nonpositiveDuration_keepsLegacyEventsOnStartDate() throws ParseException {
        List<Event> events = List.of(
                makeEvent("15/9/2026 @ 00:00", "15/9/2026 @ 00:00"),
                makeEvent("15/9/2026 @ 10:00", "15/9/2026 @ 09:00"),
                makeEvent("15/9/2026 @ 10:00", "14/9/2026 @ 09:00"),
                makeEvent("15/9/2026", "15/9/2026 @ 00:00"));

        for (Event event : events) {
            assertFalse(event.hasPositiveDuration());
            assertEquals(DATE, event.getLastOccupiedDate());
            assertFalse(event.isOnDay(DateTimeFactory.parseDateTime("14/9/2026")));
            assertTrue(event.isOnDay(DateTimeFactory.parseDateTime("15/9/2026")));
            assertFalse(event.isOnDay(DateTimeFactory.parseDateTime("16/9/2026")));
        }
    }

    @Test
    public void isOnDay_missingOneTime_usesCorrespondingDayBoundary() throws ParseException {
        List<Event> events = List.of(
                makeEvent("15/9/2026", "15/9/2026 @ 10:00"),
                makeEvent("15/9/2026 @ 22:00", "15/9/2026"));

        for (Event event : events) {
            assertFalse(event.isAllDay());
            assertTrue(event.hasPositiveDuration());
            assertTrue(event.isOnDay(DateTimeFactory.parseDateTime("15/9/2026")));
            assertFalse(event.isOnDay(DateTimeFactory.parseDateTime("16/9/2026")));
        }
    }

    /**
     * Creates an event through storage so tests can include legacy nonpositive durations.
     *
     * @param start The saved start date and optional time.
     * @param end The saved end date and optional time.
     * @return The reconstructed event.
     * @throws ParseException If the saved task type is invalid.
     */
    private Event makeEvent(String start, String end) throws ParseException {
        return (Event) TaskFactory.makeTaskFromData(new String[] {"E", "0", "Event", start, end});
    }
}
