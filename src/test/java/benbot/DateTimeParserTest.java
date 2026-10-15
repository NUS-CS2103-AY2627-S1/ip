package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests date parsing and formatting used by scheduled tasks. */
class DateTimeParserTest {
    @Test
    void parse_dateOnly_returnsStartOfDay() throws InvalidCommandException {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse("1/12/2029");

        assertEquals(LocalDateTime.of(2029, 12, 1, 0, 0), parsed.value());
        assertFalse(parsed.includesTime());
    }

    @Test
    void parse_dateAndTime_returnsDateTime() throws InvalidCommandException {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse("2/12/2019 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), parsed.value());
        assertTrue(parsed.includesTime());
    }

    @Test
    void parse_leapDay_returnsValidDate() throws InvalidCommandException {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse("29/2/2028");

        assertEquals(LocalDateTime.of(2028, 2, 29, 0, 0), parsed.value());
        assertFalse(parsed.includesTime());
    }

    @Test
    void parse_invalidDate_throwsHelpfulException() {
        InvalidCommandException exception = assertThrows(InvalidCommandException.class, () ->
                DateTimeParser.parse("31/2/2029"));

        assertTrue(exception.getMessage().startsWith("Use a date such as"));
    }

    @Test
    void parse_invalidTime_throwsHelpfulException() {
        InvalidCommandException exception = assertThrows(InvalidCommandException.class, () ->
                DateTimeParser.parse("1/12/2029 2400"));

        assertTrue(exception.getMessage().startsWith("Use a date such as"));
    }

    @Test
    void format_dateOnly_returnsDisplayDate() {
        assertEquals("Dec 01 2029", DateTimeParser.format(
                LocalDateTime.of(2029, 12, 1, 0, 0), false));
    }

    @Test
    void format_dateAndTime_returnsDisplayDateAndTime() {
        assertEquals("Dec 02 2019 6:00 pm", DateTimeParser.format(
                LocalDateTime.of(2019, 12, 2, 18, 0), true));
    }

    @Test
    void format_morningTime_returnsLowercaseAm() {
        assertEquals("Dec 02 2019 6:00 am", DateTimeParser.format(
                LocalDateTime.of(2019, 12, 2, 6, 0), true));
    }

    @Test
    void format_midnightAndNoon_returnsTwelveHourTimes() {
        assertEquals("Dec 02 2019 12:00 am", DateTimeParser.format(
                LocalDateTime.of(2019, 12, 2, 0, 0), true));
        assertEquals("Dec 02 2019 12:00 pm", DateTimeParser.format(
                LocalDateTime.of(2019, 12, 2, 12, 0), true));
    }

    @Test
    void formatForStorage_dateAndTime_returnsParseableValue() throws InvalidCommandException {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0);
        String storedValue = DateTimeParser.formatForStorage(dateTime, true);

        assertEquals("2/12/2019 1800", storedValue);
        assertEquals(dateTime, DateTimeParser.parse(storedValue).value());
    }

    @Test
    void formatForStorage_dateOnly_returnsParseableValue() throws InvalidCommandException {
        LocalDateTime dateTime = LocalDateTime.of(2029, 12, 1, 0, 0);
        String storedValue = DateTimeParser.formatForStorage(dateTime, false);
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse(storedValue);

        assertEquals("1/12/2029", storedValue);
        assertEquals(dateTime, parsed.value());
        assertFalse(parsed.includesTime());
    }
}
