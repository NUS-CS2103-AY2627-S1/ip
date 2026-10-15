package es;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DateTimeParserTest {
    @Test
    void parse_acceptsDateAndCompactTime() throws EsException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimeParser.parse("2/12/2019 1800"));
        assertEquals(LocalDateTime.of(2026, 8, 28, 0, 0), DateTimeParser.parse("2026-08-28"));
    }

    @Test
    void parse_rejectsInvalidDate() {
        assertThrows(EsException.class, () -> DateTimeParser.parse("not-a-date"));
    }

    @Test
    void format_usesReadableDateAndTime() {
        assertEquals("Dec 2 2019 6:00 pm", DateTimeParser.format(LocalDateTime.of(2019, 12, 2, 18, 0)));
        assertEquals("Aug 28 2026", DateTimeParser.format(LocalDateTime.of(2026, 8, 28, 0, 0)));
    }
}
