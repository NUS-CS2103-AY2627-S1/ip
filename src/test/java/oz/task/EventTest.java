package oz.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;

/**
 * Tests interval validation, occurrence checking, and serialization of {@link Event}.
 */
public class EventTest {

    @Test
    public void constructor_startAfterEnd_exceptionThrown() throws OzException {
        TaskDateTime start = TaskDateTime.parse("2026-09-05 1800");
        TaskDateTime end = TaskDateTime.parse("2026-09-01 1800");

        OzException exception = assertThrows(OzException.class, () ->
                new Event("project meeting", start, end));
        assertEquals("The start date/time (/from) cannot be after the end date/time (/to).",
                exception.getMessage());
    }

    @Test
    public void constructor_startEqualsEnd_success() throws OzException {
        TaskDateTime start = TaskDateTime.parse("2026-09-01 1800");
        TaskDateTime end = TaskDateTime.parse("2026-09-01 1800");

        Event event = new Event("instant event", start, end);
        assertEquals("[E][ ] instant event (from: Sep 01 2026, 6pm to: Sep 01 2026, 6pm)", event.toString());
    }

    @Test
    public void occursOn_variousDates_correctBooleanReturned() throws OzException {
        TaskDateTime start = TaskDateTime.parse("2026-09-02 0900");
        TaskDateTime end = TaskDateTime.parse("2026-09-04 1800");
        Event event = new Event("camp", start, end);

        // Before start date
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 1)));
        // On start date
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 2)));
        // In middle of event
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 3)));
        // On end date
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 4)));
        // After end date
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void toFileFormat_unmarkedAndMarked_correctFormat() throws OzException {
        TaskDateTime start = TaskDateTime.parse("2026-09-02 0900");
        TaskDateTime end = TaskDateTime.parse("2026-09-04 1800");
        Event event = new Event("camp", start, end);

        assertEquals("E | 0 | camp | 2026-09-02 0900 | 2026-09-04 1800", event.toFileFormat());

        event.markAsDone();
        assertEquals("E | 1 | camp | 2026-09-02 0900 | 2026-09-04 1800", event.toFileFormat());
    }
}
