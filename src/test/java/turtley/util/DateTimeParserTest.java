package turtley.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import turtley.exception.TurtleyException;

/**
 * Tests date and date-time parsing, formatting, and cutoff comparisons performed by
 * {@link DateTimeParser}.
 */
class DateTimeParserTest {

    /** The stable error message returned for blank, malformed, or invalid input. */
    private static final String INVALID_DATE_TIME_MESSAGE =
            "Invalid date/time format. Use yyyy-MM-dd, dd-MM-yyyy, yyyy/MM/dd, dd/MM/yyyy, "
                    + "yyyy-MM-dd HH:mm, dd-MM-yyyy HH:mm, yyyy/MM/dd HH:mm, "
                    + "dd/MM/yyyy HH:mm, yyyy-MM-dd HHmm, dd-MM-yyyy HHmm, "
                    + "yyyy/MM/dd HHmm, or dd/MM/yyyy HHmm.";

    /** The stable error message returned for unsupported temporal types. */
    private static final String UNSUPPORTED_DATE_TIME_MESSAGE =
            "Unable to format an unsupported date/time value.";

    @Test
    void parse_supportedDateFormats_returnsLocalDate() {
        LocalDate expected = LocalDate.of(2026, 8, 27);

        assertAll(
                () -> assertEquals(expected, DateTimeParser.parse("2026-08-27")),
                () -> assertEquals(expected, DateTimeParser.parse("27-08-2026")),
                () -> assertEquals(expected, DateTimeParser.parse("2026/08/27")),
                () -> assertEquals(expected, DateTimeParser.parse("27/08/2026")),
                () -> assertEquals(expected, DateTimeParser.parse("  27/08/2026  ")));
    }

    @Test
    void parse_supportedDateTimeFormats_returnsLocalDateTime() {
        LocalDateTime expected = LocalDateTime.of(2026, 8, 27, 14, 5);

        assertAll(
                () -> assertEquals(expected, DateTimeParser.parse("2026-08-27 14:05")),
                () -> assertEquals(expected, DateTimeParser.parse("27-08-2026 14:05")),
                () -> assertEquals(expected, DateTimeParser.parse("2026-08-27 1405")),
                () -> assertEquals(expected, DateTimeParser.parse("27-08-2026 1405")),
                () -> assertEquals(expected, DateTimeParser.parse("2026/08/27 14:05")),
                () -> assertEquals(expected, DateTimeParser.parse("27/08/2026 14:05")),
                () -> assertEquals(expected, DateTimeParser.parse("2026/08/27 1405")),
                () -> assertEquals(expected, DateTimeParser.parse("27/08/2026 1405")),
                () -> assertEquals(expected, DateTimeParser.parse("2026-08-27T14:05")),
                () -> assertEquals(expected, DateTimeParser.parse("27-08-2026T14:05")),
                () -> assertEquals(expected, DateTimeParser.parse("2026/08/27T14:05")),
                () -> assertEquals(expected, DateTimeParser.parse("27/08/2026T14:05")));
    }

    @Test
    void parse_nullBlankOrInvalidValue_throwsTurtleyException() {
        assertAll(
                () -> assertInvalidDateTime(null),
                () -> assertInvalidDateTime(""),
                () -> assertInvalidDateTime("   "),
                () -> assertInvalidDateTime("tomorrow"),
                () -> assertInvalidDateTime("2026-02-30"),
                () -> assertInvalidDateTime("29/02/2025"),
                () -> assertInvalidDateTime("2026-08-27 24:00"),
                () -> assertInvalidDateTime("2026-08-27 14"));
    }

    @Test
    void format_supportedTemporalTypes_returnsCanonicalText() {
        assertAll(
                () -> assertEquals("2026-08-27", DateTimeParser.format(
                        LocalDate.of(2026, 8, 27))),
                () -> assertEquals("2026-08-27 14:05", DateTimeParser.format(
                        LocalDateTime.of(2026, 8, 27, 14, 5, 37))));
    }

    @Test
    void format_unsupportedOrNullValue_throwsTurtleyException() {
        assertAll(
                () -> assertUnsupportedDateTime(YearMonth.of(2026, 8)),
                () -> assertUnsupportedDateTime(null));
    }

    @Test
    void isOnOrBefore_nullValueOrCutoff_falseReturned() {
        assertAll(
                () -> assertFalse(DateTimeParser.isOnOrBefore(null,
                        LocalDate.of(2026, 8, 27))),
                () -> assertFalse(DateTimeParser.isOnOrBefore(
                        LocalDate.of(2026, 8, 27), null)),
                () -> assertFalse(DateTimeParser.isOnOrBefore(null, null)));
    }

    @Test
    void isOnOrBefore_dateCutoff_comparesCalendarDates() {
        LocalDate cutoff = LocalDate.of(2026, 8, 27);

        assertAll(
                () -> assertTrue(DateTimeParser.isOnOrBefore(
                        LocalDate.of(2026, 8, 26), cutoff)),
                () -> assertTrue(DateTimeParser.isOnOrBefore(cutoff, cutoff)),
                () -> assertTrue(DateTimeParser.isOnOrBefore(
                        LocalDateTime.of(2026, 8, 27, 23, 59), cutoff)),
                () -> assertFalse(DateTimeParser.isOnOrBefore(
                        LocalDate.of(2026, 8, 28), cutoff)),
                () -> assertFalse(DateTimeParser.isOnOrBefore(
                        LocalDateTime.of(2026, 8, 28, 0, 1), cutoff)));
    }

    @Test
    void isOnOrBefore_dateTimeCutoff_comparesDateAndTime() {
        LocalDateTime cutoff = LocalDateTime.of(2026, 8, 27, 12, 30);

        assertAll(
                () -> assertTrue(DateTimeParser.isOnOrBefore(
                        LocalDateTime.of(2026, 8, 27, 12, 29), cutoff)),
                () -> assertTrue(DateTimeParser.isOnOrBefore(cutoff, cutoff)),
                () -> assertTrue(DateTimeParser.isOnOrBefore(
                        LocalDate.of(2026, 8, 27), cutoff)),
                () -> assertFalse(DateTimeParser.isOnOrBefore(
                        LocalDateTime.of(2026, 8, 27, 12, 31), cutoff)),
                () -> assertFalse(DateTimeParser.isOnOrBefore(
                        LocalDate.of(2026, 8, 28), cutoff)));
    }

    @Test
    void isOnOrBefore_unsupportedValue_throwsTurtleyException() {
        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> DateTimeParser.isOnOrBefore(
                        YearMonth.of(2026, 8), LocalDate.of(2026, 8, 27)));

        assertTrue(exception.getMessage().contains("unsupported date/time value"));
    }

    @Test
    void isOnOrBefore_unsupportedCutoff_throwsTurtleyException() {
        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> DateTimeParser.isOnOrBefore(
                        LocalDate.of(2026, 8, 27), YearMonth.of(2026, 8)));

        assertTrue(exception.getMessage().contains("unsupported date/time value"));
    }

    @Test
    void isBefore_requiresStrictlyEarlierValue() {
        LocalDateTime value = LocalDateTime.of(2026, 8, 27, 10, 0);

        assertAll(
                () -> assertTrue(DateTimeParser.isBefore(value,
                        LocalDateTime.of(2026, 8, 27, 10, 1))),
                () -> assertFalse(DateTimeParser.isBefore(value, value)),
                () -> assertFalse(DateTimeParser.isBefore(value,
                        LocalDateTime.of(2026, 8, 27, 9, 59))),
                () -> assertFalse(DateTimeParser.isBefore(null, value)));
    }

    /**
     * Asserts that an input is rejected with the parser's documented error message.
     *
     * @param value the input to parse.
     */
    private static void assertInvalidDateTime(String value) {
        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> DateTimeParser.parse(value));

        assertEquals(INVALID_DATE_TIME_MESSAGE, exception.getMessage());
    }

    /**
     * Asserts that a value is rejected by the formatter as an unsupported temporal type.
     *
     * @param value the value to format.
     */
    private static void assertUnsupportedDateTime(java.time.temporal.Temporal value) {
        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> DateTimeParser.format(value));

        assertEquals(UNSUPPORTED_DATE_TIME_MESSAGE, exception.getMessage());
    }
}
