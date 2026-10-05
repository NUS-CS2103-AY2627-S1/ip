package kaykay.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DateTimeParser}.
 *
 * <p>Each test follows the usual Arrange-Act-Assert structure: prepare an
 * input, call the method under test, and check that the result is correct.</p>
 */
class DateTimeParserTest {

    /** Checks ordinary dates, leap days, and the latest valid clock time. */
    @Test
    void parse_validDateTimes_returnsExpectedValues() {
        assertEquals(LocalDateTime.of(2026, 12, 25, 18, 30),
                DateTimeParser.parse("25 12 2026 18:30"));
        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0),
                DateTimeParser.parse("29 02 2024 00:00"));
        assertEquals(LocalDateTime.of(2026, 12, 31, 23, 59),
                DateTimeParser.parse("31 12 2026 23:59"));
    }

    /** Checks invalid calendar values, clock values, formats, and whitespace. */
    @Test
    void parse_invalidDateTimes_throwsDateTimeParseException() {
        String[] invalidValues = {
            "29 02 2025 12:00", // 2025 is not a leap year
            "31 04 2026 12:00", // April has only 30 days
            "00 12 2026 12:00", // day is too small
            "25 13 2026 12:00", // month is too large
            "25 12 2026 24:00", // hour is too large
            "25 12 2026 18:60", // minute is too large
            "2026-12-25 18:30", // wrong format
            " 25 12 2026 18:30", // leading whitespace
            "25 12 2026 18:30 ", // trailing whitespace
            "Friday" // free-form text
        };

        for (String invalidValue : invalidValues) {
            assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse(invalidValue),
                    "Expected parsing to fail for: " + invalidValue);
        }
    }

    /** Checks that date/time values are rendered with zero-padded fields. */
    @Test
    void format_validDateTimes_returnsStandardText() {
        assertEquals("05 01 2026 06:07",
                DateTimeParser.format(LocalDateTime.of(2026, 1, 5, 6, 7)));
        assertEquals("31 12 2026 23:59",
                DateTimeParser.format(LocalDateTime.of(2026, 12, 31, 23, 59)));
    }

    /**
     * Checks readable output and numeric storage under a non-English locale.
     */
    @Test
    void formatForDisplay_nonEnglishLocale_preservesEnglishDisplayAndStorage() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.FRANCE);
            LocalDateTime dateTime = DateTimeParser.parse("05 01 2026 06:07");
            assertEquals("05 Jan 2026, 06:07", DateTimeParser.formatForDisplay(dateTime));
            assertEquals("05 01 2026 06:07", DateTimeParser.format(dateTime));
            assertEquals("29 Feb 2024, 00:00",
                    DateTimeParser.formatForDisplay(LocalDateTime.of(2024, 2, 29, 0, 0)));
            assertEquals("31 Dec 2026, 23:59",
                    DateTimeParser.formatForDisplay(LocalDateTime.of(2026, 12, 31, 23, 59)));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}
