package oz.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;

/**
 * Tests the internal parser for task dates and times.
 */
public class TaskDateTimeParserTest {
    /** Verifies surrounding whitespace and a supported date-time are accepted. */
    @Test
    public void parse_supportedDateTimeWithWhitespace_returnsTaskDateTime()
            throws OzException {
        TaskDateTime dateTime = TaskDateTimeParser.parse(" 2/12/2019 9:15AM ");

        assertEquals("Dec 02 2019, 9:15am", dateTime.toDisplayString());
        assertEquals("2019-12-02 0915", dateTime.toStorageString());
    }

    /** Verifies the date parser accepts dates but rejects date-time input. */
    @Test
    public void parseDate_dateAndDateTime_acceptsOnlyDate() throws OzException {
        assertEquals(LocalDate.of(2026, 10, 2),
                TaskDateTimeParser.parseDate("2/10/2026"));
        assertThrows(OzException.class, () ->
                TaskDateTimeParser.parseDate("2026-10-02 1400"));
    }

    /** Verifies the time parser accepts case-insensitive 12-hour input. */
    @Test
    public void parseTime_uppercaseMeridiem_returnsLocalTime() throws OzException {
        assertEquals(LocalTime.of(9, 15), TaskDateTimeParser.parseTime("9:15AM"));
    }

    /** Verifies impossible calendar dates retain their focused error. */
    @Test
    public void parse_nonExistentDate_throwsCalendarException() {
        OzException exception = assertThrows(OzException.class, () ->
                TaskDateTimeParser.parse("2024-02-30"));

        assertEquals("That date does not exist on the calendar (e.g., February 30).",
                exception.getMessage());
    }
}
