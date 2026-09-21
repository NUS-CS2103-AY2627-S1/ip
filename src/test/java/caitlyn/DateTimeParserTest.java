package caitlyn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the supported date/time formats and conversions used by Caitlyn. */
class DateTimeParserTest {
    @Test
    void parse_acceptsIsoDateTime() {
        DateTimeParser.ParsedDateTime result = DateTimeParser.parse("2025-03-14T09:26");

        assertEquals(LocalDateTime.of(2025, 3, 14, 9, 26), result.value());
        assertTrue(result.hasTime());
    }

    @Test
    void parse_acceptsAllCommandDateTimeFormats() {
        assertEquals(LocalDateTime.of(2025, 3, 14, 9, 26),
                DateTimeParser.parse("2025-03-14 0926").value());
        assertEquals(LocalDateTime.of(2025, 3, 14, 9, 26),
                DateTimeParser.parse("14/3/2025 0926").value());
        assertEquals(LocalDateTime.of(2025, 3, 14, 9, 26),
                DateTimeParser.parse("2025-03-14 09:26").value());
        assertEquals(LocalDateTime.of(2025, 3, 14, 9, 26),
                DateTimeParser.parse("14/3/2025 09:26").value());
    }

    @Test
    void parse_acceptsDateOnlyFormatsAtMidnight() {
        DateTimeParser.ParsedDateTime isoResult = DateTimeParser.parse("2025-03-14");
        DateTimeParser.ParsedDateTime dayMonthYearResult = DateTimeParser.parse("14/3/2025");

        assertEquals(LocalDateTime.of(2025, 3, 14, 0, 0), isoResult.value());
        assertEquals(LocalDateTime.of(2025, 3, 14, 0, 0), dayMonthYearResult.value());
        assertFalse(isoResult.hasTime());
        assertFalse(dayMonthYearResult.hasTime());
    }

    @Test
    void parse_rejectsEmptyUnsupportedAndImpossibleDates() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parse(null));
        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parse("   "));
        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parse("14-03-2025"));
        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parse("2025-02-30"));
        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parse("2025-03-14 2460"));
    }

    @Test
    void formatForDisplayAndStorage_preserveDateOnlyAndDateTimeValues() {
        LocalDateTime value = LocalDateTime.of(2025, 3, 14, 9, 26);
        DateTimeParser.ParsedDateTime parsedDateTime =
                new DateTimeParser.ParsedDateTime(value, true);

        assertEquals("Mar 14 2025", DateTimeParser.formatForDisplay(value, false));
        assertEquals("Mar 14 2025 9:26 AM", DateTimeParser.formatForDisplay(value, true));
        assertEquals("2025-03-14", DateTimeParser.formatForStorage(value, false));
        assertEquals("2025-03-14T09:26", DateTimeParser.formatForStorage(value, true));
        assertEquals("Mar 14 2025 9:26 AM", DateTimeParser.formatForDisplay(parsedDateTime));
        assertEquals("2025-03-14T09:26", DateTimeParser.formatForStorage(parsedDateTime));
    }

    @Test
    void parsedDateTime_rejectsNullValue() {
        assertThrows(IllegalArgumentException.class, () -> new DateTimeParser.ParsedDateTime(null, false));
    }

    @Test
    void parse_calendarAndClockBoundaries_rejectsRollover() {
        for (String input : List.of("1900-02-29", "2027-04-31", "2027-00-15", "2027-13-15",
                "2027-01-00", "2027-01-32", "2027-01-15 2400", "2027-01-15 1260",
                "2027-01-15T09:00Z", "2027-01-15T09:00+08:00", "2027-01-15 trailing")) {
            assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parse(input), input);
        }
        assertEquals(LocalDateTime.of(2000, 2, 29, 0, 0), DateTimeParser.parse("2000-02-29").value());
    }

    @Test
    void format_midnightNoonAndSeconds_preservesDisplayAndStoragePrecision() {
        for (String[] example : List.of(
                new String[]{"2027-01-15", "Jan 15 2027"},
                new String[]{"2027-01-15T00:00", "Jan 15 2027 12:00 AM"},
                new String[]{"2027-01-15T12:00", "Jan 15 2027 12:00 PM"},
                new String[]{"2027-01-15T23:59:01.123", "Jan 15 2027 11:59:01.123 PM"},
                new String[]{"2027-01-15T18:00:30", "Jan 15 2027 6:00:30 PM"},
                new String[]{"2027-01-15T18:00:00.000000001", "Jan 15 2027 6:00:00.000000001 PM"})) {
            DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse(example[0]);
            assertEquals(example[1], DateTimeParser.formatForDisplay(parsed));
            assertEquals(example[0], DateTimeParser.formatForStorage(parsed));
            assertEquals(parsed, DateTimeParser.parse(DateTimeParser.formatForStorage(parsed)));
        }
    }

    @Test
    void parse_spacesAndTabs_preservesDateTimeValue() {
        assertEquals(LocalDateTime.of(2026, 12, 1, 18, 0),
                DateTimeParser.parse(" 2026-12-01 \t 1800 ").value());
    }
}
