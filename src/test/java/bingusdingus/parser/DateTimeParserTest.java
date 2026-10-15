package bingusdingus.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests supported date/time input formats and display/storage formatting. */
class DateTimeParserTest {
    @Test
    void parse_acceptsSupportedDateAndDateTimeFormats() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parse("2/12/2019 1800"));
        assertEquals(LocalDateTime.of(2019, 10, 15, 14, 0),
                DateTimeParser.parse("2019-10-15 1400"));
        assertEquals(LocalDateTime.of(2019, 6, 6, 0, 0),
                DateTimeParser.parse("2019-06-06"));
        assertEquals(LocalDateTime.of(2019, 8, 6, 14, 30),
                DateTimeParser.parseStorage("2019-08-06T14:30"));
    }

    @Test
    void parse_rejectsInvalidAndNullValues() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("31/02/2019"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse(null));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("2019-08-06T14:30"));
    }

    @Test
    void format_usesDateOnlyForMidnightAndIncludesTimeOtherwise() {
        assertEquals("Jun 06 2019", DateTimeParser.format(LocalDateTime.of(2019, 6, 6, 0, 0)));
        assertEquals("Jun 06 2019 2:05 PM", DateTimeParser.format(LocalDateTime.of(2019, 6, 6, 14, 5)));
        assertEquals("2019-06-06T14:05", DateTimeParser.formatForStorage(LocalDateTime.of(2019, 6, 6, 14, 5)));
    }
}
