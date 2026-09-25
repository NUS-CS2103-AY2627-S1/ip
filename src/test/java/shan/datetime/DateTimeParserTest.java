package shan.datetime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import shan.exception.InvalidArgumentException;

class DateTimeParserTest {
    private static final String INVALID_DATE_MESSAGE =
            "Use a valid date in yyyy-MM-dd format.";
    private static final String INVALID_DATE_TIME_MESSAGE =
            "Use a valid date and time in yyyy-MM-dd HH:mm or d/M/yyyy HHmm format.";

    @Test
    void parse_isoDateTime_returnsParsedDateTime() throws InvalidArgumentException {
        LocalDateTime result = DateTimeParser.parse("2019-12-02 18:00");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result);
    }

    @Test
    void parse_dayFirstDateWithCompactTime_returnsParsedDateTime()
            throws InvalidArgumentException {
        LocalDateTime result = DateTimeParser.parse("2/12/2019 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result);
    }

    @Test
    void parse_validLeapDay_returnsParsedDateTime() throws InvalidArgumentException {
        LocalDateTime result = DateTimeParser.parse("2020-02-29 09:30");

        assertEquals(LocalDateTime.of(2020, 2, 29, 9, 30), result);
    }

    @Test
    void parse_invalidCalendarDate_exceptionThrown() {
        assertInvalidDateTime("2019-02-29 09:30");
    }

    @Test
    void parse_invalidTime_exceptionThrown() {
        assertInvalidDateTime("2019-12-02 24:00");
    }

    @Test
    void parse_unsupportedFormat_exceptionThrown() {
        assertInvalidDateTime("12-02-2019 18:00");
    }

    @Test
    void parse_blankInput_exceptionThrown() {
        assertInvalidDateTime("");
    }

    @Test
    void parse_inputWithSurroundingWhitespace_exceptionThrown() {
        assertInvalidDateTime(" 2019-12-02 18:00 ");
    }

    @Test
    void parseDate_isoDate_returnsParsedDate() throws InvalidArgumentException {
        LocalDate result = DateTimeParser.parseDate("2019-12-02");

        assertEquals(LocalDate.of(2019, 12, 2), result);
    }

    @Test
    void parseDate_validLeapDay_returnsParsedDate() throws InvalidArgumentException {
        LocalDate result = DateTimeParser.parseDate("2020-02-29");

        assertEquals(LocalDate.of(2020, 2, 29), result);
    }

    @Test
    void parseDate_invalidCalendarDate_exceptionThrown() {
        assertInvalidDate("2019-02-29");
    }

    @Test
    void parseDate_unsupportedFormat_exceptionThrown() {
        assertInvalidDate("02-12-2019");
    }

    @Test
    void formatDateForDisplay_date_returnsHumanFriendlyFormat() {
        String result = DateTimeParser.formatDateForDisplay(LocalDate.of(2019, 12, 2));

        assertEquals("Dec 02 2019", result);
    }

    @Test
    void formatForDisplay_dateTime_returnsHumanFriendlyFormat() {
        String result = DateTimeParser.formatForDisplay(
                LocalDateTime.of(2019, 12, 2, 18, 0));

        assertEquals("Dec 02 2019, 6:00 PM", result);
    }

    @Test
    void formatForStorage_dateTime_returnsCanonicalFormat() {
        String result = DateTimeParser.formatForStorage(
                LocalDateTime.of(2019, 12, 2, 6, 5));

        assertEquals("2019-12-02 06:05", result);
    }

    private void assertInvalidDateTime(String input) {
        InvalidArgumentException exception = assertThrows(
                InvalidArgumentException.class, () -> DateTimeParser.parse(input));

        assertEquals(INVALID_DATE_TIME_MESSAGE, exception.getMessage());
    }

    private void assertInvalidDate(String input) {
        InvalidArgumentException exception = assertThrows(
                InvalidArgumentException.class, () -> DateTimeParser.parseDate(input));

        assertEquals(INVALID_DATE_MESSAGE, exception.getMessage());
    }
}
