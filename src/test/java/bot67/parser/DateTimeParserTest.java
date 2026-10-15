package bot67.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DateTimeParserTest {
    @Test
    void parse_acceptsDateAndBothDateTimeFormats() {
        assertEquals(LocalDateTime.of(2026, 10, 15, 0, 0), DateTimeParser.parse("2026-10-15"));
        assertEquals(LocalDateTime.of(2026, 10, 15, 14, 30),
                DateTimeParser.parse("2026-10-15T14:30"));
        assertEquals(LocalDateTime.of(2026, 10, 15, 14, 30),
                DateTimeParser.parse("2026-10-15 14:30"));
    }

    @Test
    void format_usesDateOnlyOrDateTimePattern() {
        LocalDateTime value = LocalDateTime.of(2026, 10, 5, 9, 7);
        assertEquals("Oct 5 2026", DateTimeParser.format(value, false));
        assertEquals("Oct 5 2026 09:07", DateTimeParser.format(value, true));
    }
}
