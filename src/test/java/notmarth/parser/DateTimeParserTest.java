package notmarth.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests the supported date/time input formats and archive/display formats. */
class DateTimeParserTest {
    /** Verifies parsing of compact date-time and date-only values. */
    @Test
    void parse_compactDateTimeAndDateOnlyInput_returnsParsedValues() {
        DateTimeParser.ParsedDateTime dateTime = DateTimeParser.parse("2/12/2019 1800");
        DateTimeParser.ParsedDateTime date = DateTimeParser.parse("2019-10-15");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), dateTime.value());
        assertTrue(dateTime.hasTime());
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), date.value());
        assertFalse(date.hasTime());
    }

    /** Verifies parsing of ISO date-time values and clock values with a colon. */
    @Test
    void parse_isoDateTimeAndColonClockInput_returnsParsedValues() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 14, 5),
                DateTimeParser.parse("2019-10-15 14:05").value());
        assertEquals(LocalDateTime.of(2019, 10, 15, 14, 5),
                DateTimeParser.parse("2019-10-15T14:05").value());
    }

    /** Verifies rejection of date-time input where a date-only value is required. */
    @Test
    void dateParsing_timedOrInvalidInput_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parseDate("2019-10-15 1400"));
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parse("31/2/2019"));
    }

    /** Verifies that missing and irregularly spaced date values are rejected. */
    @Test
    void parse_missingOrIrregularWhitespace_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse(null));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse(" 2019-10-15"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("2019-10-15  1400"));
    }

    /** Verifies that display and storage formatting preserve time presence. */
    @Test
    void format_valuesWithOrWithoutTime_preservesTimePresence() {
        LocalDateTime value = LocalDateTime.of(2019, 10, 15, 14, 0);

        assertEquals("Oct 15 2019", DateTimeParser.format(value, false));
        assertEquals("Oct 15 2019 2:00 PM", DateTimeParser.format(value, true));
        assertEquals("2019-10-15", DateTimeParser.formatForStorage(value, false));
        assertEquals("2019-10-15T14:00", DateTimeParser.formatForStorage(value, true));
        assertEquals(LocalDate.of(2019, 10, 15), DateTimeParser.parseDate("15/10/2019"));
    }
}
