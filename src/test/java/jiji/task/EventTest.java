package jiji.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Event}.
 */
public class EventTest {

    @Test
    public void constructor_validDates_success() {
        Event event = new Event("camp", "2026-08-29", "2026-08-31");
        assertEquals("camp", event.getDescription());
        assertEquals(LocalDate.of(2026, 8, 29), event.getStartDate());
        assertEquals(LocalDate.of(2026, 8, 31), event.getEndDate());
        assertFalse(event.isDone());
        assertEquals("[E][ ] camp (from: Aug 29 2026 to: Aug 31 2026)", event.toString());
        assertEquals("E | 0 | camp | 2026-08-29 | 2026-08-31", event.toFileFormat());
    }

    @Test
    public void constructor_validDateTimes_success() {
        Event event = new Event("hackathon", "2026-08-30 0900", "2026-08-30 1800");
        assertEquals("hackathon", event.getDescription());
        assertEquals(LocalDate.of(2026, 8, 30), event.getStartDate());
        assertEquals(LocalDate.of(2026, 8, 30), event.getEndDate());
        assertEquals("[E][ ] hackathon (from: Aug 30 2026, 9:00AM to: Aug 30 2026, 6:00PM)", event.toString());
        assertEquals("E | 0 | hackathon | 2026-08-30 0900 | 2026-08-30 1800", event.toFileFormat());
    }

    @Test
    public void constructor_rawStringDates_success() {
        Event event = new Event("meeting", "Mon 2pm", "4pm");
        assertNull(event.getStartDate());
        assertNull(event.getEndDate());
        assertEquals("[E][ ] meeting (from: Mon 2pm to: 4pm)", event.toString());
        assertEquals("E | 0 | meeting | Mon 2pm | 4pm", event.toFileFormat());
    }

    @Test
    public void markAndUnmark_updatesStatusAndFileFormat() {
        Event event = new Event("camp", "2026-08-29", "2026-08-31");
        event.markAsDone();
        assertTrue(event.isDone());
        assertEquals("[E][X] camp (from: Aug 29 2026 to: Aug 31 2026)", event.toString());
        assertEquals("E | 1 | camp | 2026-08-29 | 2026-08-31", event.toFileFormat());

        event.markAsNotDone();
        assertFalse(event.isDone());
        assertEquals("[E][ ] camp (from: Aug 29 2026 to: Aug 31 2026)", event.toString());
        assertEquals("E | 0 | camp | 2026-08-29 | 2026-08-31", event.toFileFormat());
    }

    @Test
    public void occursOn_withinDateRange_returnsTrue() {
        Event event = new Event("symposium", "2026-08-29", "2026-08-31");
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 29)));
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 30)));
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 31)));
        assertFalse(event.occursOn(LocalDate.of(2026, 8, 28)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 1)));
        assertFalse(event.occursOn(null));
    }

    @Test
    public void occursOn_singleKnownDate_matchesOnlyKnownDate() {
        Event startOnly = new Event("kickoff", "2026-08-29", "TBD");
        assertTrue(startOnly.occursOn(LocalDate.of(2026, 8, 29)));
        assertFalse(startOnly.occursOn(LocalDate.of(2026, 8, 30)));

        Event endOnly = new Event("wrapup", "TBD", "2026-08-31");
        assertTrue(endOnly.occursOn(LocalDate.of(2026, 8, 31)));
        assertFalse(endOnly.occursOn(LocalDate.of(2026, 8, 30)));

        Event neither = new Event("chill", "soon", "later");
        assertFalse(neither.occursOn(LocalDate.of(2026, 8, 30)));
    }

    @Test
    public void constructor_nullOrBlankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Event(null, "2026-08-29", "2026-08-31"));
        assertThrows(AssertionError.class, () -> new Event("   ", "2026-08-29", "2026-08-31"));
    }

    @Test
    public void constructor_nullOrBlankFromOrTo_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Event("camp", null, "2026-08-31"));
        assertThrows(AssertionError.class, () -> new Event("camp", "   ", "2026-08-31"));
        assertThrows(AssertionError.class, () -> new Event("camp", "2026-08-29", null));
        assertThrows(AssertionError.class, () -> new Event("camp", "2026-08-29", "   "));
    }
}
