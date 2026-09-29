package tasktracker.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import tasktracker.exception.TaskTrackerException;
import tasktracker.ui.Message;

public class TaskDateTimeTest {

    @Test
    public void constructor_validStandardDateTime_parsedCorrectly() throws TaskTrackerException {
        TaskDateTime dt = new TaskDateTime("2026-12-25 1800");
        assertEquals("Dec 25 2026, 6:00 PM", dt.toString());
        assertEquals("Dec 25 2026, 6:00 PM", dt.toDisplayString());
        assertEquals("2026-12-25 1800", dt.toFileString());
        assertNotNull(dt.getDateTime());
    }

    @Test
    public void constructor_paddedWithWhitespace_parsedCorrectly() throws TaskTrackerException {
        TaskDateTime dt = new TaskDateTime("   2026-08-09 0930   ");
        assertEquals("Aug 9 2026, 9:30 AM", dt.toDisplayString());
        assertEquals("2026-08-09 0930", dt.toFileString());
    }

    @Test
    public void constructor_nullInput_throwsTaskTrackerException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            new TaskDateTime(null);
        });
        assertEquals(Message.ERR_INVALID_DATE_TIME, ex.getMessage());
    }

    @Test
    public void constructor_emptyOrBlankInput_throwsTaskTrackerException() {
        assertThrows(TaskTrackerException.class, () -> new TaskDateTime(""));
        assertThrows(TaskTrackerException.class, () -> new TaskDateTime("   "));
    }

    @Test
    public void constructor_strictResolverNonExistentDates_throwsTaskTrackerException() {
        // February 30th does not exist
        assertThrows(TaskTrackerException.class, () -> new TaskDateTime("2026-02-30 1200"));

        // April 31st does not exist
        assertThrows(TaskTrackerException.class, () -> new TaskDateTime("2026-04-31 1200"));

        // 2026 is not a leap year, so Feb 29 is invalid
        assertThrows(TaskTrackerException.class, () -> new TaskDateTime("2026-02-29 1200"));
    }

    @Test
    public void constructor_leapYearValidDate_parsedCorrectly() throws TaskTrackerException {
        // 2028 is a leap year
        TaskDateTime dt = new TaskDateTime("2028-02-29 2359");
        assertEquals("Feb 29 2028, 11:59 PM", dt.toDisplayString());
    }

    @Test
    public void constructor_invalidTimeValues_throwsTaskTrackerException() {
        // Invalid hour (24:00)
        assertThrows(TaskTrackerException.class, () -> new TaskDateTime("2026-12-25 2400"));

        // Invalid minute (60)
        assertThrows(TaskTrackerException.class, () -> new TaskDateTime("2026-12-25 1260"));
    }

    @Test
    public void constructor_incorrectDelimitersOrSlashFormat_throwsTaskTrackerException() {
        assertThrows(TaskTrackerException.class, () -> new TaskDateTime("25/12/2026 1800"));
        assertThrows(TaskTrackerException.class, () -> new TaskDateTime("2026.12.25 1800"));
        assertThrows(TaskTrackerException.class, () -> new TaskDateTime("2026-12-25 18:00"));
    }

    @Test
    public void isAfter_comparison_returnsCorrectBoolean() throws TaskTrackerException {
        TaskDateTime earlier = new TaskDateTime("2026-05-10 1000");
        TaskDateTime later = new TaskDateTime("2026-05-10 1100");
        TaskDateTime duplicate = new TaskDateTime("2026-05-10 1000");

        assertTrue(later.isAfter(earlier));
        assertFalse(earlier.isAfter(later));
        assertFalse(earlier.isAfter(duplicate)); // strictly after, so equality returns false
    }
}
