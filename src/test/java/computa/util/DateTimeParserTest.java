package computa.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the date parsing and formatting rules used by Computa. */
class DateTimeParserTest {

    @Test
    void parse_supportedDateFormats_returnsExpectedDateTime() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), DateTimeParser.parse("2019-10-15"));
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), DateTimeParser.parse("2019-10-15 1800"));
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), DateTimeParser.parse("2019-10-15 18:00"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimeParser.parse("2/12/2019 1800"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimeParser.parse("2019-12-02T18:00"));
    }

    @Test
    void parse_freeFormOrImpossibleDate_returnsNull() {
        assertNull(DateTimeParser.parse("Sunday"));
        assertNull(DateTimeParser.parse("2019-02-30"));
        assertNull(DateTimeParser.parse(null));
    }

    @Test
    void looksLikeDate_identifiesStructuredDateText() {
        assertTrue(DateTimeParser.looksLikeDate("2019-10-15"));
        assertTrue(DateTimeParser.looksLikeDate("2/12/2019 1800"));
        assertFalse(DateTimeParser.looksLikeDate("Sunday"));
        assertFalse(DateTimeParser.looksLikeDate(null));
    }

    @Test
    void parseQueryDate_supportedDateFormats_returnsDate() {
        assertEquals(LocalDate.of(2019, 10, 15), DateTimeParser.parseQueryDate("2019-10-15"));
        assertEquals(LocalDate.of(2019, 12, 2), DateTimeParser.parseQueryDate("2/12/2019"));
        assertNull(DateTimeParser.parseQueryDate("2019-02-30"));
        assertNull(DateTimeParser.parseQueryDate(null));
    }

    @Test
    void hasTime_dateAndDateTimeText_reportsCorrectly() {
        assertFalse(DateTimeParser.hasTime("2019-10-15"));
        assertTrue(DateTimeParser.hasTime("2019-10-15 1800"));
        assertTrue(DateTimeParser.hasTime("2/12/2019 18:00"));
        assertTrue(DateTimeParser.hasTime("2019-10-15T18:00"));
        assertFalse(DateTimeParser.hasTime(null));
    }

    @Test
    void formatForDisplay_dateAndDateTime_usesReadableFormats() {
        LocalDateTime value = LocalDateTime.of(2019, 10, 15, 18, 0);

        assertEquals("Oct 15 2019", DateTimeParser.formatForDisplay(value, false));
        assertEquals("Oct 15 2019 1800", DateTimeParser.formatForDisplay(value, true));
        assertEquals("Oct 15 2019", DateTimeParser.formatDateForDisplay(value.toLocalDate()));
    }

    @Test
    void formatForStorage_dateAndDateTime_usesCanonicalFormats() {
        LocalDateTime value = LocalDateTime.of(2019, 10, 15, 18, 0);

        assertEquals("2019-10-15", DateTimeParser.formatForStorage(value, false));
        assertEquals("2019-10-15 1800", DateTimeParser.formatForStorage(value, true));
    }
}
