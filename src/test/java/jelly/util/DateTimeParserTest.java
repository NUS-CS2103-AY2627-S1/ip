package jelly.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests user-facing and stored date-time parsing. */
class DateTimeParserTest {
    @Test
    void parse_validInput_returnsDateTime() {
        assertEquals(LocalDateTime.of(2026, 2, 28, 9, 5), DateTimeParser.parse("2026-02-28 0905"));
        assertEquals(LocalDateTime.of(2026, 2, 28, 9, 5),
                DateTimeParser.parseStored("2026-02-28T09:05:00"));
    }

    @Test
    void parse_invalidInput_throwsParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("2026-02-30 0905"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("2026-02-28 9:05"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseStored("not-a-date"));
    }
}
