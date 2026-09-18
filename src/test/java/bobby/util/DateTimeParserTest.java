package bobby.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import bobby.BobbyException;

/**
 * Tests date/time parsing and display formatting used by task commands.
 */
class DateTimeParserTest {
    @Test
    void parse_supportedDateAndDateTimeFormats_returnsLocalDateTime() throws BobbyException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                DateTimeParser.parse("2019-12-02"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parse("2019-12-02 1800"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parse("  2019-12-02   1800  "));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parse("2/12/2019 1800"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parse("2019-12-02T18:00"));
    }

    @Test
    void parse_invalidDateTime_throwsBobbyException() {
        assertThrows(BobbyException.class, () -> DateTimeParser.parse("no idea"));
        assertThrows(BobbyException.class, () -> DateTimeParser.parse("2019-02-30"));
        assertThrows(BobbyException.class, () -> DateTimeParser.parse("30/2/2019 1800"));
    }

    @Test
    void formatForDisplay_midnightAndNonMidnightDateTimes_returnsReadableText() {
        assertEquals("Dec 2 2019", DateTimeParser.formatForDisplay(
                LocalDateTime.of(2019, 12, 2, 0, 0)));
        assertEquals("Dec 2 2019, 12:00pm", DateTimeParser.formatForDisplay(
                LocalDateTime.of(2019, 12, 2, 12, 0)));
        assertEquals("Dec 2 2019, 6:00pm", DateTimeParser.formatForDisplay(
                LocalDateTime.of(2019, 12, 2, 18, 0)));
    }
}
