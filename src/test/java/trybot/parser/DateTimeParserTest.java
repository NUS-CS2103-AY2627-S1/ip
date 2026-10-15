package trybot.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the supported inputs and validation behavior of {@link DateTimeParser}.
 */
class DateTimeParserTest {

    @Test
    void parseOrNull_supportedDateFormats_returnsDateAtStartOfDay() {
        assertAll(
                () -> assertParsed("2024-02-29", LocalDateTime.of(2024, 2, 29, 0, 0), false),
                () -> assertParsed("29/2/2024", LocalDateTime.of(2024, 2, 29, 0, 0), false),
                () -> assertParsed("29-2-2024", LocalDateTime.of(2024, 2, 29, 0, 0), false));
    }

    @Test
    void parseOrNull_supportedDateTimeFormats_returnsDateTimeWithTimeFlag() {
        assertAll(
                () -> assertParsed("2024-02-29 1830", LocalDateTime.of(2024, 2, 29, 18, 30), true),
                () -> assertParsed("2024-02-29 18:30", LocalDateTime.of(2024, 2, 29, 18, 30), true),
                () -> assertParsed("29/2/2024 1830", LocalDateTime.of(2024, 2, 29, 18, 30), true),
                () -> assertParsed("29/2/2024 18:30", LocalDateTime.of(2024, 2, 29, 18, 30), true),
                () -> assertParsed("29-2-2024 1830", LocalDateTime.of(2024, 2, 29, 18, 30), true),
                () -> assertParsed("29-2-2024 18:30", LocalDateTime.of(2024, 2, 29, 18, 30), true));
    }

    @Test
    void parseOrNull_legacyText_returnsNull() {
        assertAll(
                () -> assertNull(DateTimeParser.parseOrNull("Friday")),
                () -> assertNull(DateTimeParser.parseOrNull("  next week  ")));
    }

    @Test
    void parseOrNull_nullOrBlankInput_throwsIllegalArgumentException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parseOrNull(null)),
                () -> assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parseOrNull("")),
                () -> assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parseOrNull("   ")));
    }

    @Test
    void parseOrNull_invalidNumericInput_throwsIllegalArgumentException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DateTimeParser.parseOrNull("31/02/2019")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DateTimeParser.parseOrNull("2019-12-01 2500")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DateTimeParser.parseOrNull("2024-02-29 12:60")));
    }

    @Test
    void formatForDisplay_dateOnlyAndDateTime_returnsHumanReadableText() {
        assertAll(
                () -> assertEquals("Feb 29 2024",
                        DateTimeParser.formatForDisplay(
                                new DateTimeParser.ParsedDateTime(
                                        LocalDateTime.of(2024, 2, 29, 0, 0), false))),
                () -> assertEquals("Feb 29 2024 18:30",
                        DateTimeParser.formatForDisplay(
                                new DateTimeParser.ParsedDateTime(
                                        LocalDateTime.of(2024, 2, 29, 18, 30), true))));
    }

    @Test
    void formatForStorage_dateOnlyAndDateTime_returnsCanonicalText() {
        assertAll(
                () -> assertEquals("2024-02-29",
                        DateTimeParser.formatForStorage(
                                new DateTimeParser.ParsedDateTime(
                                        LocalDateTime.of(2024, 2, 29, 0, 0), false))),
                () -> assertEquals("2024-02-29 1830",
                        DateTimeParser.formatForStorage(
                                new DateTimeParser.ParsedDateTime(
                                        LocalDateTime.of(2024, 2, 29, 18, 30), true))));
    }

    @Test
    void formatForDisplayOrStorage_nullValue_exceptionThrown() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DateTimeParser.formatForDisplay(null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DateTimeParser.formatForStorage(null)));
    }

    @Test
    void parsedDateTime_nullDateTime_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new DateTimeParser.ParsedDateTime(null, false));
    }

    /**
     * Verifies both the parsed date-time and whether the input included a time.
     *
     * @param input date or date-time text to parse.
     * @param expectedDateTime expected parsed value.
     * @param expectedHasTime expected time-presence flag.
     */
    private static void assertParsed(String input, LocalDateTime expectedDateTime, boolean expectedHasTime) {
        DateTimeParser.ParsedDateTime actual = DateTimeParser.parseOrNull(input);
        assertEquals(expectedDateTime, actual.dateTime());
        assertEquals(expectedHasTime, actual.hasTime());
    }
}
