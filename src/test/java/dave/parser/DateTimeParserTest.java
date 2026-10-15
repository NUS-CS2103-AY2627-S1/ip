package dave.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import dave.exception.DaveCommandException;

/**
 * Unit tests for the {@link DateTimeParser} class.
 */
public class DateTimeParserTest {

    @Test
    public void parse_standardDateTimeWithColon_success() {
        ParsedDateTime result = DateTimeParser.parse("2026-09-15 18:30");
        assertEquals(LocalDateTime.of(2026, 9, 15, 18, 30), result.getDateTime());
        assertTrue(result.hasTime());
    }

    @Test
    public void parse_dateTimeWithoutColon_success() {
        ParsedDateTime result = DateTimeParser.parse("2026-12-31 2359");
        assertEquals(LocalDateTime.of(2026, 12, 31, 23, 59), result.getDateTime());
        assertTrue(result.hasTime());
    }

    @Test
    public void parse_dateOnly_success() {
        ParsedDateTime result = DateTimeParser.parse("2026-10-20");
        assertEquals(LocalDateTime.of(2026, 10, 20, 0, 0), result.getDateTime());
        assertFalse(result.hasTime());
    }

    @Test
    public void parse_invalidFormat_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> DateTimeParser.parse("tomorrow"));
        assertThrows(DaveCommandException.class, () -> DateTimeParser.parse("15/09/2026"));
        assertThrows(DaveCommandException.class, () -> DateTimeParser.parse(""));
    }

    @Test
    public void parse_nonExistentDateFeb30_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () ->
                DateTimeParser.parse("2026-02-30"));
        assertTrue(e.getMessage().contains("date does not exist on the calendar"));
    }

    @Test
    public void parse_nonExistentDateApr31_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () ->
                DateTimeParser.parse("2026-04-31 12:00"));
        assertTrue(e.getMessage().contains("date does not exist on the calendar"));
    }

    @Test
    public void parse_leapYearFeb29_success() {
        ParsedDateTime result = DateTimeParser.parse("2024-02-29");
        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), result.getDateTime());
    }

    @Test
    public void parse_nonLeapYearFeb29_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () ->
                DateTimeParser.parse("2025-02-29"));
        assertTrue(e.getMessage().contains("date does not exist on the calendar"));
    }
}
