package bro.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import bro.exception.BroException;

/**
 * Unit tests for {@link Event}.
 */
public class EventTest {

    @Test
    public void constructor_validDates_success() throws BroException {
        Event event = new Event("project meeting", "2026-08-28", "2026-08-30");
        assertNotNull(event);
        assertEquals("project meeting", event.getDescription());
        assertEquals("project meeting", event.getTask());
        assertFalse(event.isDone());
        assertNotNull(event.getStart());
        assertNotNull(event.getEnd());
    }

    @Test
    public void constructor_validDateTimes_success() throws BroException {
        Event event = new Event("symposium", "28/8/2026 0900", "28/8/2026 1700");
        assertNotNull(event);
        assertEquals("symposium", event.getDescription());
        assertEquals("symposium", event.getTask());
        assertTrue(event.getStart().hasTime());
        assertTrue(event.getEnd().hasTime());
    }

    @Test
    public void constructor_rawStrings_success() throws BroException {
        Event event = new Event("hangout", "morning", "afternoon");
        assertNotNull(event);
        assertEquals("morning", event.getStart().getRawString());
        assertEquals("afternoon", event.getEnd().getRawString());
    }

    @Test
    public void constructor_emptyDate_exceptionThrown() {
        assertThrows(BroException.class, () -> new Event("meeting", "", "2026-08-30"));
        assertThrows(BroException.class, () -> new Event("meeting", "2026-08-28", ""));
        assertThrows(BroException.class, () -> new Event("meeting", null, "2026-08-30"));
        assertThrows(BroException.class, () -> new Event("meeting", "2026-08-28", null));
    }

    @Test
    public void isOnDate_dateWithinRange_returnsTrue() throws BroException {
        Event event = new Event("hackathon", "2026-08-28", "2026-08-30");
        assertTrue(event.isOnDate(LocalDate.of(2026, 8, 28))); // start date
        assertTrue(event.isOnDate(LocalDate.of(2026, 8, 29))); // middle date
        assertTrue(event.isOnDate(LocalDate.of(2026, 8, 30))); // end date
    }

    @Test
    public void isOnDate_dateOutsideRange_returnsFalse() throws BroException {
        Event event = new Event("hackathon", "2026-08-28", "2026-08-30");
        assertFalse(event.isOnDate(LocalDate.of(2026, 8, 27))); // before
        assertFalse(event.isOnDate(LocalDate.of(2026, 8, 31))); // after
    }

    @Test
    public void isOnDate_singleParsedDate_handledCorrectly() throws BroException {
        // Start parsed, end is raw string
        Event eventWithParsedStart = new Event("workshop", "2026-08-28", "end of day");
        assertTrue(eventWithParsedStart.isOnDate(LocalDate.of(2026, 8, 28)));
        assertFalse(eventWithParsedStart.isOnDate(LocalDate.of(2026, 8, 29)));

        // End parsed, start is raw string
        Event eventWithParsedEnd = new Event("workshop", "morning", "2026-08-30");
        assertTrue(eventWithParsedEnd.isOnDate(LocalDate.of(2026, 8, 30)));
        assertFalse(eventWithParsedEnd.isOnDate(LocalDate.of(2026, 8, 29)));

        // Both unparseable raw strings
        Event eventWithRawStrings = new Event("party", "today", "tomorrow");
        assertFalse(eventWithRawStrings.isOnDate(LocalDate.of(2026, 8, 28)));
    }

    @Test
    public void toString_uncompletedEvent_formattedStringReturned() throws BroException {
        Event event = new Event("project meeting", "2026-08-28", "2026-08-30");
        assertEquals("[E][ ] project meeting (from: Aug 28 2026 to: Aug 30 2026)", event.toString());
    }

    @Test
    public void toString_completedEventWithTimes_formattedStringReturned() throws BroException {
        Event event = new Event("meeting", "28/8/2026 1400", "28/8/2026 1600");
        event.markDone();
        assertEquals("[E][X] meeting (from: Aug 28 2026, 2:00PM to: Aug 28 2026, 4:00PM)", event.toString());
    }

    @Test
    public void toFileFormat_uncompletedEvent_formattedStringReturned() throws BroException {
        Event event = new Event("project meeting", "2026-08-28", "2026-08-30");
        assertEquals("E | 0 | project meeting | 2026-08-28 | 2026-08-30", event.toFileFormat());
    }

    @Test
    public void toFileFormat_completedEventWithTimes_formattedStringReturned() throws BroException {
        Event event = new Event("meeting", "28/8/2026 1400", "28/8/2026 1600");
        event.markDone();
        assertEquals("E | 1 | meeting | 2026-08-28 1400 | 2026-08-28 1600", event.toFileFormat());
    }

    @Test
    public void markDoneAndUnmarkDone_togglesCompletionStatus() throws BroException {
        Event event = new Event("workshop", "2026-08-28", "2026-08-29");
        assertFalse(event.isDone());

        event.markDone();
        assertTrue(event.isDone());
        assertEquals("E | 1 | workshop | 2026-08-28 | 2026-08-29", event.toFileFormat());

        event.unmarkDone();
        assertFalse(event.isDone());
        assertEquals("E | 0 | workshop | 2026-08-28 | 2026-08-29", event.toFileFormat());
    }

    @Test
    public void containsKeywords_matchingKeywords_returnsTrue() throws BroException {
        Event event = new Event("Developer Conference 2026", "2026-09-01", "2026-09-03");
        assertTrue(event.containsKeywords("developer"));
        assertTrue(event.containsKeywords("CONFERENCE"));
        assertTrue(event.containsKeywords("2026"));
        assertFalse(event.containsKeywords("hackathon"));
    }

    @Test
    public void setDescription_validDescription_descriptionUpdated() throws BroException {
        Event event = new Event("project meeting", "2026-08-28", "2026-08-30");
        event.setDescription("team sync");
        assertEquals("team sync", event.getDescription());
        assertEquals("[E][ ] team sync (from: Aug 28 2026 to: Aug 30 2026)", event.toString());
        assertEquals("E | 0 | team sync | 2026-08-28 | 2026-08-30", event.toFileFormat());
    }

    @Test
    public void setStart_string_startUpdated() throws BroException {
        Event event = new Event("project meeting", "2026-08-28", "2026-08-30");
        event.setStart("2026-08-29");
        assertFalse(event.isOnDate(LocalDate.of(2026, 8, 28)));
        assertTrue(event.isOnDate(LocalDate.of(2026, 8, 29)));
        assertEquals("[E][ ] project meeting (from: Aug 29 2026 to: Aug 30 2026)", event.toString());
        assertEquals("E | 0 | project meeting | 2026-08-29 | 2026-08-30", event.toFileFormat());
    }

    @Test
    public void setEnd_string_endUpdated() throws BroException {
        Event event = new Event("project meeting", "2026-08-28", "2026-08-30");
        event.setEnd("2026-09-02");
        assertTrue(event.isOnDate(LocalDate.of(2026, 9, 1)));
        assertEquals("[E][ ] project meeting (from: Aug 28 2026 to: Sep 02 2026)", event.toString());
        assertEquals("E | 0 | project meeting | 2026-08-28 | 2026-09-02", event.toFileFormat());
    }

    @Test
    public void setStartAndEnd_taskDateTime_timesUpdated() throws BroException {
        Event event = new Event("project meeting", "2026-08-28", "2026-08-30");
        event.setStart(TaskDateTime.parse("2026-09-10 1000"));
        event.setEnd(TaskDateTime.parse("2026-09-10 1200"));
        assertEquals("[E][ ] project meeting (from: Sep 10 2026, 10:00AM to: Sep 10 2026, 12:00PM)",
                event.toString());
        assertEquals("E | 0 | project meeting | 2026-09-10 1000 | 2026-09-10 1200", event.toFileFormat());
    }

    @Test
    public void setStartAndEnd_invalidString_exceptionThrown() throws BroException {
        Event event = new Event("project meeting", "2026-08-28", "2026-08-30");
        assertThrows(BroException.class, () -> event.setStart(""));
        assertThrows(BroException.class, () -> event.setEnd(""));
    }

    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        BroException e1 = assertThrows(BroException.class, () ->
                new Event("meeting", "2026-08-30", "2026-08-28"));
        assertTrue(e1.getMessage().contains("can't end before it even starts"));

        BroException e2 = assertThrows(BroException.class, () ->
                new Event("meeting", "2026-08-28 1800", "2026-08-28 1700"));
        assertTrue(e2.getMessage().contains("can't end before it even starts"));
    }

    @Test
    public void validateChronologicalOrder_endBeforeStart_exceptionThrown() throws BroException {
        TaskDateTime start = TaskDateTime.parse("2026-08-30");
        TaskDateTime end = TaskDateTime.parse("2026-08-28");
        BroException e = assertThrows(BroException.class, () ->
                Event.validateChronologicalOrder(start, end));
        assertTrue(e.getMessage().contains("can't end before it even starts"));
    }

    @Test
    public void equalsAndHashCode_matchingAndDifferent_behaveCorrectly() throws BroException {
        Event e1 = new Event("hackathon", "2026-08-28", "2026-08-30");
        Event e2 = new Event("HACKATHON", "2026-08-28", "2026-08-30");
        Event e3 = new Event("hackathon", "2026-08-29", "2026-08-30");
        Event e4 = new Event("hackathon", "2026-08-28", "2026-08-31");

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
        assertFalse(e1.equals(e3));
        assertFalse(e1.equals(e4));
        assertFalse(e1.equals(null));
    }
}

