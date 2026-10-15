package shai.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests supported date/time input formats and output formats. */
class DateTimeParserTest {
    @Test
    void parse_supportedDateTimeFormats_returnsExpectedDateTime() {
        LocalDateTime expected = LocalDateTime.of(2019, 12, 3, 14, 5);

        assertEquals(expected, DateTimeParser.parse("2019-12-03 1405"));
        assertEquals(expected, DateTimeParser.parse("2019-12-03 14:05"));
        assertEquals(expected, DateTimeParser.parse("3/12/2019 1405"));
        assertEquals(expected, DateTimeParser.parse("3/12/2019 14:05"));
    }

    @Test
    void parse_dateOnlyValue_returnsStartOfDay() {
        LocalDateTime expected = LocalDateTime.of(2019, 12, 3, 0, 0);

        assertEquals(expected, DateTimeParser.parse("2019-12-03"));
        assertEquals(expected, DateTimeParser.parse("3/12/2019"));
    }

    @Test
    void parse_invalidOrNullValue_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("2019-02-29"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse(null));
    }

    @Test
    void formatForDisplay_midnightValue_omitsTime() {
        LocalDateTime date = LocalDateTime.of(2019, 12, 3, 0, 0);

        assertEquals("Dec 03 2019", DateTimeParser.formatForDisplay(date));
    }

    @Test
    void formatForDisplay_nonMidnightValue_includesTime() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 3, 14, 5);

        assertEquals("Dec 03 2019, 2:05 PM", DateTimeParser.formatForDisplay(dateTime));
    }

    @Test
    void formatForStorage_dateTimeValue_usesStableFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 3, 14, 5);

        assertEquals("2019-12-03 1405", DateTimeParser.formatForStorage(dateTime));
    }
}
