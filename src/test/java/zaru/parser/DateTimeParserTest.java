package zaru.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import zaru.exception.ZaruException;

/** Tests date-time parsing and formatting used by deadline and event tasks. */
public class DateTimeParserTest {
    /** Verifies parsing of date and time input. */
    @Test
    public void parse_dateTimeInput_returnsDateTime() throws ZaruException {
        LocalDateTime result = DateTimeParser.parse("2026-08-19 1430");

        assertEquals(LocalDateTime.of(2026, 8, 19, 14, 30), result);
    }

    /** Verifies that date-only input is interpreted as midnight. */
    @Test
    public void parse_dateOnlyInput_returnsMidnight() throws ZaruException {
        LocalDateTime result = DateTimeParser.parse("2026-08-19");

        assertEquals(LocalDateTime.of(2026, 8, 19, 0, 0), result);
    }

    /** Verifies the user-facing date-time display format. */
    @Test
    public void format_dateTime_returnsDisplayFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 19, 14, 30);

        assertEquals("Aug 19 2026, 2:30PM", DateTimeParser.format(dateTime));
    }

    /** Verifies the stable date-time format used in save files. */
    @Test
    public void formatForStorage_dateTime_returnsStableStorageFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 19, 14, 30);

        assertEquals("2026-08-19 1430", DateTimeParser.formatForStorage(dateTime));
    }

    /** Verifies that blank date input is rejected. */
    @Test
    public void parse_blankInput_exceptionThrown() {
        ZaruException exception = assertThrows(ZaruException.class, () -> DateTimeParser.parse(" "));

        assertEquals("Please provide a date and time.", exception.getMessage());
    }

    /** Verifies that unsupported date input is rejected. */
    @Test
    public void parse_invalidInput_exceptionThrown() {
        ZaruException exception = assertThrows(ZaruException.class, () ->
                DateTimeParser.parse("19-08-2026"));

        assertEquals("Please enter dates in yyyy-MM-dd or yyyy-MM-dd HHmm format.", exception.getMessage());
    }

    /** Verifies that a calendar date which does not exist is rejected. */
    @Test
    public void parse_nonExistentDate_exceptionThrown() {
        ZaruException exception = assertThrows(ZaruException.class, () ->
                DateTimeParser.parse("2026-02-30 1200"));

        assertEquals("Please enter dates in yyyy-MM-dd or yyyy-MM-dd HHmm format.", exception.getMessage());
    }
}
