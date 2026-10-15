package alex.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import alex.exception.AlexException;

/**
 * Tests parsing of ISO-formatted dates.
 */
public class DateParserTest {
    @Test
    public void parse_validDate_returnsLocalDate() throws AlexException {
        LocalDate expectedDate = LocalDate.of(2026, 8, 28);

        LocalDate actualDate = DateParser.parse("2026-08-28");

        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void parse_validLeapDay_returnsLocalDate() throws AlexException {
        LocalDate expectedDate = LocalDate.of(2024, 2, 29);

        LocalDate actualDate = DateParser.parse("2024-02-29");

        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void parse_invalidFormat_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                DateParser.parse("28-08-2026"));

        assertEquals("That date didn't scan. Use yyyy-MM-dd, like 2026-09-20.",
                exception.getMessage());
    }

    @Test
    public void parse_impossibleDate_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                DateParser.parse("2026-02-30"));

        assertEquals("That date didn't scan. Use yyyy-MM-dd, like 2026-09-20.",
                exception.getMessage());
    }

    @Test
    public void parse_nonLeapYearFebruary29_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                DateParser.parse("2025-02-29"));

        assertEquals("That date didn't scan. Use yyyy-MM-dd, like 2026-09-20.",
                exception.getMessage());
    }

    @Test
    public void parse_emptyInput_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                DateParser.parse(""));

        assertEquals("That date didn't scan. Use yyyy-MM-dd, like 2026-09-20.",
                exception.getMessage());
    }

    @Test
    public void parseTaskDateTime_dateWithoutTime_returnsDateOnly() throws AlexException {
        TaskDateTime expectedDateTime = new TaskDateTime(LocalDate.of(2019, 12, 2));

        TaskDateTime actualDateTime = DateParser.parseTaskDateTime("2019-12-02");

        assertEquals(expectedDateTime, actualDateTime);
    }

    @Test
    public void parseTaskDateTime_dateWithTime_returnsDateAndTime() throws AlexException {
        TaskDateTime expectedDateTime = new TaskDateTime(
                LocalDate.of(2019, 12, 2), LocalTime.of(18, 0));

        TaskDateTime actualDateTime = DateParser.parseTaskDateTime("2019-12-02 1800");

        assertEquals(expectedDateTime, actualDateTime);
    }

    @Test
    public void parseTaskDateTime_impossibleDate_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                DateParser.parseTaskDateTime("2019-02-29 1800"));

        assertEquals("I couldn't read that. Use yyyy-MM-dd and optionally HHmm, "
                + "like 2026-09-20 1830.", exception.getMessage());
    }

    @Test
    public void parseTaskDateTime_impossibleTime_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                DateParser.parseTaskDateTime("2019-12-02 2400"));

        assertEquals("I couldn't read that. Use yyyy-MM-dd and optionally HHmm, "
                + "like 2026-09-20 1830.", exception.getMessage());
    }
}
