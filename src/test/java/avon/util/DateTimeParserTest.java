package avon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

class DateTimeParserTest {
    @Test
    void parse_validDateTime_returnsLocalDateTime() {
        assertEquals(LocalDateTime.of(2026, 8, 20, 18, 0),
                DateTimeParser.parse("2026-08-20 1800"));
    }

    @Test
    void parse_dateOnly_returnsMidnight() {
        assertEquals(LocalDateTime.of(2026, 8, 20, 0, 0),
                DateTimeParser.parse("2026-08-20"));
    }

    @Test
    void parse_invalidDateAndTime_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("2026-02-30 2500"));
    }

    @Test
    void parse_nullValue_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> DateTimeParser.parse(null));
    }

    @Test
    void parseStoredValue_isoDateTime_restoresLocalDateTime() {
        assertEquals(LocalDateTime.of(2026, 8, 20, 18, 0),
                DateTimeParser.parseStoredValue("2026-08-20T18:00"));
    }

    @Test
    void parseStoredValue_legacyDate_restoresMidnight() {
        assertEquals(LocalDateTime.of(2026, 8, 20, 0, 0),
                DateTimeParser.parseStoredValue("2026-08-20"));
    }

    @Test
    void parseStoredValue_nullValue_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> DateTimeParser.parseStoredValue(null));
    }

    @Test
    void format_midnight_omitsTime() {
        assertEquals("Aug 20 2026",
                DateTimeParser.format(LocalDateTime.of(2026, 8, 20, 0, 0)));
    }

    @Test
    void format_nonMidnight_includesReadableTime() {
        assertEquals("Aug 20 2026, 6:00PM",
                DateTimeParser.format(LocalDateTime.of(2026, 8, 20, 18, 0)));
    }
}
