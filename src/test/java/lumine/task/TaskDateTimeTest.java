package lumine.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import lumine.LumineException;

/**
 * Tests the shared parsing and formatting behavior of {@link TaskDateTime}.
 */
class TaskDateTimeTest {

    @Test
    void formatForDisplay_freeText_returnsOriginalText() {
        TaskDateTime taskDateTime = new TaskDateTime("Monday 5pm");

        assertEquals("Monday 5pm", taskDateTime.formatForDisplay());
    }

    @Test
    void formatForStorage_dateWithExtraSpaces_returnsCanonicalDate() {
        TaskDateTime taskDateTime = new TaskDateTime("2026  01  02");

        assertEquals("2026 01 02", taskDateTime.formatForStorage());
    }

    @Test
    void formatForDisplay_dateTime_returnsHumanReadableDateTime() {
        TaskDateTime taskDateTime = new TaskDateTime("2026 01 02 1530");

        assertEquals("Jan 02 2026 15:30", taskDateTime.formatForDisplay());
        assertEquals(LocalDate.of(2026, 1, 2), taskDateTime.toLocalDate());
    }

    @Test
    void constructor_invalidCalendarDate_throwsDateNotFound() {
        LumineException exception = assertThrows(LumineException.class, () -> new TaskDateTime("2026 02 30"));

        assertEquals("Date not found :<.\nPlease enter a valid calendar date or time.", exception.getMessage());
    }

    @Test
    void constructor_invalidCalendarTime_throwsDateNotFound() {
        assertThrows(LumineException.class, () -> new TaskDateTime("2026 02 28 2460"));
    }

    @Test
    void formatForStorage_validLeapDay_returnsCanonicalDate() {
        TaskDateTime taskDateTime = new TaskDateTime("2024 02 29");

        assertEquals("2024 02 29", taskDateTime.formatForStorage());
    }
}
