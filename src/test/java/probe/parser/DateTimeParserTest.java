package probe.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DateTimeParserTest {

    @Test
    void parsesDateAtStartOfDay() {
        LocalDateTime result = DateTimeParser.parse("2026-08-28");

        assertEquals(LocalDateTime.of(2026, 8, 28, 0, 0), result);
    }

    @Test
    void parsesDateAndTime() {
        LocalDateTime result = DateTimeParser.parse("28/8/2026 1430");

        assertEquals(LocalDateTime.of(2026, 8, 28, 14, 30), result);
    }

    @Test
    void trimsWhitespaceBeforeParsing() {
        LocalDateTime result = DateTimeParser.parse("  2026-08-28  ");

        assertEquals(LocalDateTime.of(2026, 8, 28, 0, 0), result);
    }

    @Test
    void rejectsInvalidDateOrTime() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DateTimeParser.parse("not a date"));

        assertEquals("Invalid date/time. Use yyyy-MM-dd or d/M/yyyy HHmm.",
                exception.getMessage());
    }

    @Test
    void formatsDateTimeForDisplay() {
        LocalDateTime value = LocalDateTime.of(2026, 8, 28, 14, 30);

        assertEquals("Aug 28 2026, 2:30pm", DateTimeParser.formatForDisplay(value));
    }

    @Test
    void formatsDateTimeForStorage() {
        LocalDateTime value = LocalDateTime.of(2026, 8, 28, 14, 30);

        assertEquals("2026-08-28T14:30", DateTimeParser.formatForStorage(value));
    }
}
