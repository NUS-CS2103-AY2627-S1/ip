package axiom.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import axiom.AxiomException;

class DateTimeParserTest {

    @Test
    void parse_isoDate_returnsStartOfDay() throws AxiomException {
        assertEquals(LocalDateTime.of(2019, 6, 6, 0, 0), DateTimeParser.parse("2019-06-06"));
    }

    @Test
    void parse_slashDateWithTime_returnsDateTime() throws AxiomException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimeParser.parse("2/12/2019 1800"));
    }

    @Test
    void parse_slashDateWithColonTime_returnsDateTime() throws AxiomException {
        assertEquals(LocalDateTime.of(2019, 8, 6, 14, 0), DateTimeParser.parse("6/8/2019 14:00"));
    }

    @Test
    void parse_isoDateWithTime_returnsDateTime() throws AxiomException {
        assertEquals(LocalDateTime.of(2019, 8, 6, 14, 0), DateTimeParser.parse("2019-08-06 1400"));
    }

    @Test
    void parse_isoDateTimeWithT_returnsDateTime() throws AxiomException {
        assertEquals(LocalDateTime.of(2019, 8, 6, 14, 0), DateTimeParser.parse("2019-08-06T14:00"));
    }

    @Test
    void parse_slashDateOnly_returnsStartOfDay() throws AxiomException {
        assertEquals(LocalDateTime.of(2019, 6, 6, 0, 0), DateTimeParser.parse("6/6/2019"));
    }

    @Test
    void parse_leadingAndTrailingWhitespace_trimsInput() throws AxiomException {
        assertEquals(LocalDateTime.of(2019, 6, 6, 0, 0), DateTimeParser.parse("  2019-06-06  "));
    }

    @Test
    void parse_invalidFormat_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> DateTimeParser.parse("no idea :-p"));
        assertEquals("Invalid date/time format: 'no idea :-p'. Use yyyy-MM-dd or d/M/yyyy HHmm.",
                exception.getMessage());
    }

    @Test
    void parse_invalidCalendarDate_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> DateTimeParser.parse("2019-02-30"));
        assertEquals("'2019-02-30' is not a valid date or time.", exception.getMessage());
    }

    @Test
    void parse_invalidSlashCalendarDate_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> DateTimeParser.parse("30/2/2019"));
        assertEquals("'30/2/2019' is not a valid date or time.", exception.getMessage());
    }

    @Test
    void parse_invalidTime_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> DateTimeParser.parse("2/12/2019 1860"));
        assertEquals("'2/12/2019 1860' is not a valid date or time.", exception.getMessage());
    }

    @Test
    void parseStored_validIsoString_returnsDateTime() throws AxiomException {
        assertEquals(LocalDateTime.of(2019, 6, 6, 0, 0),
                DateTimeParser.parseStored("2019-06-06T00:00"));
    }

    @Test
    void parseStored_withWhitespace_trimsInput() throws AxiomException {
        assertEquals(LocalDateTime.of(2019, 8, 6, 14, 0),
                DateTimeParser.parseStored("  2019-08-06T14:00  "));
    }

    @Test
    void parseStored_invalidFormat_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> DateTimeParser.parseStored("not-a-datetime"));
        assertEquals("Invalid stored date/time: 'not-a-datetime'.", exception.getMessage());
    }

    @Test
    void format_midnight_returnsDateOnly() {
        assertEquals("Jun 06 2019", DateTimeParser.format(LocalDateTime.of(2019, 6, 6, 0, 0)));
    }

    @Test
    void format_withTime_returnsDateAndTime() {
        assertEquals("Dec 02 2019, 6:00 PM",
                DateTimeParser.format(LocalDateTime.of(2019, 12, 2, 18, 0)));
    }

    @Test
    void formatStored_dateTime_returnsIsoString() {
        assertEquals("2019-08-06T14:00",
                DateTimeParser.formatStored(LocalDateTime.of(2019, 8, 6, 14, 0)));
    }

    @Test
    void formatStored_roundTrip_matchesParseStored() throws AxiomException {
        LocalDateTime original = LocalDateTime.of(2019, 8, 6, 14, 0);
        assertEquals(original, DateTimeParser.parseStored(DateTimeParser.formatStored(original)));
    }
}
