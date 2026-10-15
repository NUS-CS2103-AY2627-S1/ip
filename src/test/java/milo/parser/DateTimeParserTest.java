package milo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class DateTimeParserTest {

    @Test
    public void parse_isoDateTime_expectedLocalDateTime() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0),
                DateTimeParser.parse("2019-10-15 1800"));
    }

    @Test
    public void parse_slashDateTime_expectedLocalDateTime() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parse("2/12/2019 1800"));
    }

    @Test
    public void parse_dateOnly_expectedMidnightAndDateDisplay() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0),
                DateTimeParser.parse("2019-10-15"));
        assertEquals("Oct 15 2019",
                DateTimeParser.formatForDisplay(LocalDateTime.of(2019, 10, 15, 0, 0)));
    }

    @Test
    public void formatForDisplayAndStorage_dateTime_expectedReadableAndCanonicalFormats() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 10, 15, 18, 0);

        assertEquals("Oct 15 2019, 6:00 PM", DateTimeParser.formatForDisplay(dateTime));
        assertEquals("2019-10-15 1800", DateTimeParser.formatForStorage(dateTime));
    }

    @Test
    public void parse_invalidDate_expectedParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("2019-02-30 1800"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse(""));
    }
}
