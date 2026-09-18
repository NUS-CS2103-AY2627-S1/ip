package lune.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

/**
 * Tests every method Event itself defines or overrides:
 * {@link Event#occursOn(LocalDate)}, {@link Event#toString()}, and
 * {@link Event#toSaveFormat()}. The constructor and inherited members
 * (getDescription, markAsDone, etc.) aren't tested separately here since
 * they belong to Task and are exercised transitively by these tests.
 */
public class EventTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2019, 12, 1, 0, 0);
    private static final LocalDateTime TO = LocalDateTime.of(2019, 12, 5, 0, 0);

    // Mirrors Task's private display formatters. Built independently here
    // (rather than hardcoding a literal like "6:00 pm") so these tests
    // don't depend on the JVM's default locale for AM/PM casing.
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    // --- occursOn ---

    @Test
    public void occursOn_dateBeforeEventRange_falseReturned() {
        Event event = new Event("conference", FROM, TO);
        assertFalse(event.occursOn(LocalDate.of(2019, 11, 30)));
    }

    @Test
    public void occursOn_dateAtEventStart_trueReturned() {
        Event event = new Event("conference", FROM, TO);
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_dateInsideEventRange_trueReturned() {
        Event event = new Event("conference", FROM, TO);
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_dateAtEventEnd_trueReturned() {
        Event event = new Event("conference", FROM, TO);
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 5)));
    }

    @Test
    public void occursOn_dateAfterEventRange_falseReturned() {
        Event event = new Event("conference", FROM, TO);
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 6)));
    }

    @Test
    public void occursOn_singleDayEventMatchingDate_trueReturned() {
        LocalDateTime sameDay = LocalDateTime.of(2019, 12, 2, 0, 0);
        Event event = new Event("team meeting", sameDay, sameDay);
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_singleDayEventDifferentDate_falseReturned() {
        LocalDateTime sameDay = LocalDateTime.of(2019, 12, 2, 0, 0);
        Event event = new Event("team meeting", sameDay, sameDay);
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_eventWithTimeComponents_timeOfDayIgnored() {
        // from/to carry non-midnight times; occursOn should still match by
        // date alone, regardless of the time-of-day attached to either end.
        Event event = new Event("team meeting",
                LocalDateTime.of(2019, 12, 2, 9, 0),
                LocalDateTime.of(2019, 12, 2, 17, 30));
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 2)));
    }

    // --- toString ---

    @Test
    public void toString_notDoneEventWithMidnightTimes_dateOnlyDisplayed() {
        Event event = new Event("conference", FROM, TO);
        String expected = "[E][ ] conference (from: " + FROM.format(DATE_FORMAT)
                + " to: " + TO.format(DATE_FORMAT) + ")";
        assertEquals(expected, event.toString());
    }

    @Test
    public void toString_doneEvent_statusIconXShown() {
        Event event = new Event("conference", FROM, TO);
        event.markAsDone();
        String expected = "[E][X] conference (from: " + FROM.format(DATE_FORMAT)
                + " to: " + TO.format(DATE_FORMAT) + ")";
        assertEquals(expected, event.toString());
    }

    @Test
    public void toString_eventWithTimeComponents_dateAndTimeDisplayed() {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 9, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 18, 0);
        Event event = new Event("team meeting", from, to);
        String expected = "[E][ ] team meeting (from: " + from.format(DATE_TIME_FORMAT)
                + " to: " + to.format(DATE_TIME_FORMAT) + ")";
        assertEquals(expected, event.toString());
    }

    @Test
    public void toString_fromHasTimeToDoesNot_eachFieldFormattedIndependently() {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 9, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 5, 0, 0);
        Event event = new Event("multi-day trip", from, to);
        String expected = "[E][ ] multi-day trip (from: " + from.format(DATE_TIME_FORMAT)
                + " to: " + to.format(DATE_FORMAT) + ")";
        assertEquals(expected, event.toString());
    }

    // --- toSaveFormat ---

    @Test
    public void toSaveFormat_notDoneEvent_pipeSeparatedLineWithZeroFlag() {
        Event event = new Event("conference", FROM, TO);
        assertEquals("E | 0 | conference | " + FROM + " | " + TO, event.toSaveFormat());
    }

    @Test
    public void toSaveFormat_doneEvent_pipeSeparatedLineWithOneFlag() {
        Event event = new Event("conference", FROM, TO);
        event.markAsDone();
        assertEquals("E | 1 | conference | " + FROM + " | " + TO, event.toSaveFormat());
    }

    @Test
    public void toSaveFormat_eventWithTimeComponents_isoDateTimeIncluded() {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 9, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 17, 30);
        Event event = new Event("team meeting", from, to);
        assertEquals("E | 0 | team meeting | " + from + " | " + to, event.toSaveFormat());
    }
}
