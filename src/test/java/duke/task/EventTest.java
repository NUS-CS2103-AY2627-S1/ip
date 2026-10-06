package duke.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import duke.exception.DukeException;

/**
 * Unit tests for the {@link Event} class.
 */
public class EventTest {

    @Test
    public void constructor_validDateAndTime_success() throws DukeException {
        Event event = new Event("project meeting", "2026-09-01 1400", "2026-09-01 1600");
        assertEquals("E | 0 | project meeting | 2026-09-01T14:00 | 2026-09-01T16:00", event.toFileFormat());
        assertEquals("project meeting (from: Sep 1 2026, 2:00 PM to: Sep 1 2026, 4:00 PM)", event.toString());
    }

    @Test
    public void constructor_validDateOnly_success() throws DukeException {
        Event event = new Event("carnival", "2026-09-01", "2026-09-02");
        assertEquals("E | 0 | carnival | 2026-09-01T00:00 | 2026-09-02T00:00", event.toFileFormat());
    }

    @Test
    public void constructor_invalidStartDateFormat_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () ->
                new Event("meeting", "01-09-2026", "2026-09-01 1600"));
        assertEquals(
                "Invalid date format. Please use yyyy-MM-dd or yyyy-MM-dd HHmm (e.g., 2019-10-15 1800).",
                exception.getMessage());
    }

    @Test
    public void constructor_invalidEndDateFormat_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () ->
                new Event("meeting", "2026-09-01 1400", "invalid-date"));
        assertEquals(
                "Invalid date format. Please use yyyy-MM-dd or yyyy-MM-dd HHmm (e.g., 2019-10-15 1800).",
                exception.getMessage());
    }

    @Test
    public void getTaskIcon_returnsE() throws DukeException {
        Event event = new Event("meeting", "2026-09-01 1400", "2026-09-01 1600");
        assertEquals("E", event.getTaskIcon());
    }

    @Test
    public void toFileFormat_markedEvent_formattedCorrectly() throws DukeException {
        Event event = new Event("meeting", "2026-09-01 1400", "2026-09-01 1600");
        event.markAsDone();
        assertEquals("E | 1 | meeting | 2026-09-01T14:00 | 2026-09-01T16:00", event.toFileFormat());
    }

    @Test
    public void constructor_startAfterEnd_exceptionThrown() {
        DukeException ex = assertThrows(DukeException.class, () ->
                new Event("meeting", "2026-09-02 1400", "2026-09-01 1400"));
        assertEquals("OOPS!!! The event start time cannot be after the end time.", ex.getMessage());
    }

    @Test
    public void equals_andHashCode_operateCorrectly() throws DukeException {
        Event event1 = new Event("meeting", "2026-09-01 1400", "2026-09-01 1600");
        Event event2 = new Event("meeting", "2026-09-01 1400", "2026-09-01 1600");
        Event event3 = new Event("diff", "2026-09-01 1400", "2026-09-01 1600");

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        org.junit.jupiter.api.Assertions.assertNotEquals(event1, event3);
        org.junit.jupiter.api.Assertions.assertNotEquals(event1, null);
    }
}
