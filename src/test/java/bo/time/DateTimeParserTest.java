package bo.time;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the supported date and date-time input handled by {@link DateTimeParser}. */
class DateTimeParserTest {
    /** Verifies each documented date and date-time input family. */
    @Test
    void parse_supportedDateFormats_returnsExpectedValues() {
        assertAll(
                () -> assertParsed("2/12/2019 1800", LocalDateTime.of(2019, 12, 2, 18, 0), true),
                () -> assertParsed("2019-10-15 1800", LocalDateTime.of(2019, 10, 15, 18, 0), true),
                () -> assertParsed("2019-10-15 18:00", LocalDateTime.of(2019, 10, 15, 18, 0), true),
                () -> assertParsed("2019-10-15T18:00:30", LocalDateTime.of(2019, 10, 15, 18, 0, 30), true),
                () -> assertParsed("2019-10-15", LocalDateTime.of(2019, 10, 15, 0, 0), false),
                () -> assertParsed("15/10/2019", LocalDateTime.of(2019, 10, 15, 0, 0), false));
    }

    /** Verifies that harmless surrounding whitespace is ignored. */
    @Test
    void parse_inputWithSurroundingWhitespace_trimsBeforeParsing() {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse("  2019-10-15  ");

        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), parsed.value());
        assertFalse(parsed.includesTime());
    }

    /** Verifies that repeated spaces inside a date/time are treated as separators. */
    @Test
    void parse_inputWithRepeatedInternalWhitespace_normalizesBeforeParsing() {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse("2/12/2019    1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), parsed.value());
    }

    /** Verifies that null, empty, and whitespace-only input is rejected. */
    @Test
    void parse_blankInput_exceptionThrown() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parse(null)),
                () -> assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parse("")),
                () -> assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parse("   ")));
    }

    /** Verifies that invalid dates, times, and unsupported formats are rejected. */
    @Test
    void parse_invalidInput_exceptionThrown() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DateTimeParser.parse("31/2/2019")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DateTimeParser.parse("2019-02-29")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DateTimeParser.parse("2019-10-15 2500")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DateTimeParser.parse("June 6th")));
    }

    /** Verifies that date-only values use Bo's human-readable display format. */
    @Test
    void format_dateOnly_returnsHumanReadableDate() {
        DateTimeParser.ParsedDateTime parsed = new DateTimeParser.ParsedDateTime(
                LocalDateTime.of(2019, 10, 15, 0, 0), false);

        assertEquals("Oct 15 2019", DateTimeParser.format(parsed));
    }

    /** Verifies that date-time values include the time in the display format. */
    @Test
    void format_dateAndTime_returnsHumanReadableDateAndTime() {
        DateTimeParser.ParsedDateTime parsed = new DateTimeParser.ParsedDateTime(
                LocalDateTime.of(2019, 10, 15, 9, 5), true);

        assertEquals("Oct 15 2019 09:05", DateTimeParser.format(parsed));
    }

    /** Verifies that date-only values are serialized as ISO dates. */
    @Test
    void serialize_dateOnly_returnsIsoDate() {
        DateTimeParser.ParsedDateTime parsed = new DateTimeParser.ParsedDateTime(
                LocalDateTime.of(2019, 10, 15, 0, 0), false);

        assertEquals("2019-10-15", DateTimeParser.serialize(parsed));
    }

    /** Verifies that date-time values are serialized as ISO local date-times. */
    @Test
    void serialize_dateAndTime_returnsIsoDateTime() {
        DateTimeParser.ParsedDateTime parsed = new DateTimeParser.ParsedDateTime(
                LocalDateTime.of(2019, 10, 15, 9, 5, 30), true);

        assertEquals("2019-10-15T09:05:30", DateTimeParser.serialize(parsed));
    }

    /** Asserts both the parsed value and whether the input included a time. */
    private static void assertParsed(String input, LocalDateTime expectedValue, boolean expectedIncludesTime) {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse(input);

        assertEquals(expectedValue, parsed.value());
        assertEquals(expectedIncludesTime, parsed.includesTime());
    }
}
