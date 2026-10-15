package bubu.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import bubu.exception.InvalidDateTimeException;

/** Tests the date-time formats accepted by the application. */
class DateTimeParserTest {
    private static final LocalTime DEFAULT_TIME = LocalTime.of(23, 59);

    @Test
    void parse_fullDateTime_returnsParsedValue() throws InvalidDateTimeException {
        LocalDateTime result = DateTimeParser.parse("2026-09-18 1830", DEFAULT_TIME);

        assertEquals(LocalDateTime.of(2026, 9, 18, 18, 30), result);
    }

    @Test
    void parse_dateOnly_usesDefaultTime() throws InvalidDateTimeException {
        LocalDateTime result = DateTimeParser.parse("2026-09-18", DEFAULT_TIME);

        assertEquals(LocalDateTime.of(2026, 9, 18, 23, 59), result);
    }

    @Test
    void parse_invalidDateOrTime_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () ->
                DateTimeParser.parse("2026-02-30", DEFAULT_TIME));
        assertThrows(InvalidDateTimeException.class, () ->
                DateTimeParser.parse("2026-09-18 2460", DEFAULT_TIME));
    }

    @Test
    void parseStored_invalidValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                DateTimeParser.parseStored("not-a-date", DEFAULT_TIME));
    }

    @Test
    void format_parsedValue_returnsStorageFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 9, 18, 18, 30);

        assertEquals("2026-09-18 1830", DateTimeParser.format(dateTime));
    }
}
