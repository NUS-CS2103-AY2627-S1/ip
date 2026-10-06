package com.gihun456.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class DateTimeParserTest {
    @Test
    public void parseUserDateTime_supportedDateTimeFormats_returnsDateTime() {
        LocalDateTime expected = LocalDateTime.of(2026, 9, 15, 10, 30);

        assertEquals(expected, DateTimeParser.parseUserDateTime("15/09/2026 1030"));
        assertEquals(expected, DateTimeParser.parseUserDateTime("2026-09-15 1030"));
    }

    @Test
    public void parseUserDateTime_supportedDateFormats_returnsMidnight() {
        LocalDateTime expected = LocalDateTime.of(2026, 9, 15, 0, 0);

        assertEquals(expected, DateTimeParser.parseUserDateTime("15/09/2026"));
        assertEquals(expected, DateTimeParser.parseUserDateTime("2026-09-15"));
    }

    @Test
    public void parseUserDateTime_invalidInput_throwsParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseUserDateTime("15-09-2026"));
    }

    @Test
    public void storageDateTime_roundTrip_preservesValue() {
        LocalDateTime original = LocalDateTime.of(2026, 9, 15, 10, 30, 45);

        String stored = DateTimeParser.formatForStorage(original);

        assertEquals(original, DateTimeParser.parseStoredDateTime(stored));
    }
}
