package aider.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import aider.AiderException;

/** Tests accepted date formats and invalid date handling. */
class DateTimeParserTest {
    @Test
    void parse_acceptsSupportedDateFormats() throws Exception {
        assertEquals(LocalDateTime.of(2026, 9, 1, 0, 0),
                DateTimeParser.parse("2026-09-01"));
        assertEquals(LocalDateTime.of(2026, 9, 1, 9, 30),
                DateTimeParser.parse("2026-09-01 0930"));
        assertEquals(LocalDateTime.of(2026, 9, 1, 9, 30),
                DateTimeParser.parse("1/9/2026 0930"));
    }

    @Test
    void parse_rejectsMissingAndImpossibleDates() {
        assertThrows(AiderException.class, () -> DateTimeParser.parse(null));
        assertThrows(AiderException.class, () -> DateTimeParser.parse(""));
        assertThrows(AiderException.class, () -> DateTimeParser.parse("2026-02-30"));
    }

    @Test
    void format_distinguishesDateOnlyAndDateTimeValues() {
        assertEquals("Sep 01 2026", DateTimeParser.format(LocalDateTime.of(2026, 9, 1, 0, 0)));
        assertEquals("Sep 01 2026 09:30",
                DateTimeParser.format(LocalDateTime.of(2026, 9, 1, 9, 30)));
    }
}
