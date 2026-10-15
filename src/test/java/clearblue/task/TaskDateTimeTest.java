package clearblue.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaskDateTime}, covering the date-vs-free-text parsing
 * and formatting behavior used by Deadline and Event.
 */
public class TaskDateTimeTest {
    @Test
    public void constructor_validIsoDate_isRecognizedAsDate() {
        TaskDateTime dateTime = new TaskDateTime("2019-06-06");

        assertTrue(dateTime.isDate());
        assertEquals(LocalDate.of(2019, 6, 6), dateTime.getDate());
        assertEquals("Jun 06 2019", dateTime.toDisplayString());
        assertEquals("2019-06-06", dateTime.toStorageString());
    }

    @Test
    public void constructor_freeText_isNotRecognizedAsDate() {
        TaskDateTime dateTime = new TaskDateTime("Sunday");

        assertFalse(dateTime.isDate());
        assertEquals("Sunday", dateTime.toDisplayString());
        assertEquals("Sunday", dateTime.toStorageString());
    }

    @Test
    public void constructor_emptyText_isNotRecognizedAsDate() {
        TaskDateTime dateTime = new TaskDateTime("");

        assertFalse(dateTime.isDate());
        assertEquals("", dateTime.toDisplayString());
    }

    @Test
    public void constructor_singleDigitMonthAndDay_isNotRecognizedAsDate() {
        // yyyy-MM-dd requires two-digit month/day; a value like this should
        // fall back to being treated as free text, not silently misparsed.
        TaskDateTime dateTime = new TaskDateTime("2019-6-6");

        assertFalse(dateTime.isDate());
        assertEquals("2019-6-6", dateTime.toDisplayString());
    }

    @Test
    public void constructor_dateWithInvalidDayOfMonth_isNotRecognizedAsDate() {
        // April has only 30 days; a naive parser might accept this anyway.
        TaskDateTime dateTime = new TaskDateTime("2019-04-31");

        assertFalse(dateTime.isDate());
    }

    @Test
    public void getDate_onFreeTextValue_throwsIllegalStateException() {
        TaskDateTime dateTime = new TaskDateTime("no idea :-p");

        assertThrows(IllegalStateException.class, dateTime::getDate);
    }

    @Test
    public void toString_matchesDisplayString() {
        TaskDateTime dateTime = new TaskDateTime("2019-12-25");

        assertEquals(dateTime.toDisplayString(), dateTime.toString());
    }

    @Test
    public void parseDate_validIsoDate_returnsParsedDate() {
        assertEquals(LocalDate.of(2019, 6, 6), TaskDateTime.parseDate("2019-06-06"));
    }

    @Test
    public void parseDate_nonDateText_returnsNull() {
        assertNull(TaskDateTime.parseDate("not a date"));
    }

    @Test
    public void formatDate_matchesDisplayFormat() {
        assertEquals("Jun 06 2019", TaskDateTime.formatDate(LocalDate.of(2019, 6, 6)));
    }
}
