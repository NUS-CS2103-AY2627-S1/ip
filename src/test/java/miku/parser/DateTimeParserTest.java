package miku.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import miku.MikuException;

/** Tests the supported parsing and display formatting behaviour of {@link DateTimeParser}. */
class DateTimeParserTest {

    @Test
    void parse_isoDate_returnsMidnightAndIndicatesNoTime() throws MikuException {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse("2024-02-29");

        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), parsed.dateTime());
        assertFalse(parsed.hasTime());
    }

    @Test
    void parse_isoDateTime_returnsValueAndIndicatesTime() throws MikuException {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse("2024-02-29 2359");

        assertEquals(LocalDateTime.of(2024, 2, 29, 23, 59), parsed.dateTime());
        assertTrue(parsed.hasTime());
    }

    @Test
    void parse_slashDate_returnsMidnightAndIndicatesNoTime() throws MikuException {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse("3/7/2024");

        assertEquals(LocalDateTime.of(2024, 7, 3, 0, 0), parsed.dateTime());
        assertFalse(parsed.hasTime());
    }

    @Test
    void parse_slashDateTime_returnsValueAndIndicatesTime() throws MikuException {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse("3/7/2024 0015");

        assertEquals(LocalDateTime.of(2024, 7, 3, 0, 15), parsed.dateTime());
        assertTrue(parsed.hasTime());
    }

    @Test
    void parse_invalidCalendarDate_exceptionThrown() {
        assertThrows(MikuException.class, () -> DateTimeParser.parse("2023-02-29"));
        assertThrows(MikuException.class, () -> DateTimeParser.parse("31/4/2024"));
    }

    @Test
    void parse_invalidTime_exceptionThrown() {
        assertThrows(MikuException.class, () -> DateTimeParser.parse("2024-01-01 2400"));
        assertThrows(MikuException.class, () -> DateTimeParser.parse("1/1/2024 1260"));
    }

    @Test
    void parse_unsupportedInputShape_exceptionThrown() {
        assertThrows(MikuException.class, () -> DateTimeParser.parse("2024/01/01"));
        assertThrows(MikuException.class, () -> DateTimeParser.parse("2024-1-1"));
        assertThrows(MikuException.class, () -> DateTimeParser.parse("2024-01-01 12:00"));
        assertThrows(MikuException.class, () -> DateTimeParser.parse(""));
    }

    @Test
    void format_dateOnly_returnsDisplayDateWithoutImpliedMidnight() {
        String formatted = DateTimeParser.format(LocalDateTime.of(2024, 7, 3, 0, 0), false);

        assertEquals("Jul 03 2024", formatted);
    }

    @Test
    void format_dateTime_returnsTwelveHourDisplayTime() {
        String formatted = DateTimeParser.format(LocalDateTime.of(2024, 7, 3, 13, 5), true);

        assertEquals("Jul 03 2024 1:05 PM", formatted);
    }
}
