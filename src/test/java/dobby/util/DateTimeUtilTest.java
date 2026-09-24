package dobby.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import dobby.exception.DobbyException;

/** Tests date and time parsing used by deadline and event tasks. */
class DateTimeUtilTest {
    @Test
    void parse_nullInput_assertionError() {
        assertThrows(AssertionError.class, () -> DateTimeUtil.parse(null));
    }

    @Test
    void parse_isoDate_returnsStartOfDayWithoutTime() throws DobbyException {
        DateTimeUtil.ParsedDateTime parsed = DateTimeUtil.parse("2019-12-03");

        assertEquals(LocalDateTime.of(2019, 12, 3, 0, 0), parsed.getValue());
        assertFalse(parsed.hasTime());
    }

    @Test
    void parse_slashDate_returnsStartOfDayWithoutTime() throws DobbyException {
        DateTimeUtil.ParsedDateTime parsed = DateTimeUtil.parse("3/12/2019");

        assertEquals(LocalDateTime.of(2019, 12, 3, 0, 0), parsed.getValue());
        assertFalse(parsed.hasTime());
    }

    @Test
    void parse_compactTime_returnsDateAndTime() throws DobbyException {
        DateTimeUtil.ParsedDateTime parsed = DateTimeUtil.parse("2019-12-03 0900");

        assertEquals(LocalDateTime.of(2019, 12, 3, 9, 0), parsed.getValue());
        assertTrue(parsed.hasTime());
    }

    @Test
    void parse_colonSeparatedTime_returnsDateAndTime() throws DobbyException {
        DateTimeUtil.ParsedDateTime parsed = DateTimeUtil.parse("3/12/2019 09:00");

        assertEquals(LocalDateTime.of(2019, 12, 3, 9, 0), parsed.getValue());
        assertTrue(parsed.hasTime());
    }

    @Test
    void parse_surroundingWhitespace_ignoresWhitespace() throws DobbyException {
        DateTimeUtil.ParsedDateTime parsed = DateTimeUtil.parse("  2019-12-03   0900  ");

        assertEquals(LocalDateTime.of(2019, 12, 3, 9, 0), parsed.getValue());
        assertTrue(parsed.hasTime());
    }

    @Test
    void parse_invalidDate_exceptionThrown() {
        assertThrows(DobbyException.class, () -> DateTimeUtil.parse("2019-02-29"));
    }

    @Test
    void parse_invalidTime_exceptionThrown() {
        assertThrows(DobbyException.class, () -> DateTimeUtil.parse("2019-12-03 2400"));
    }

    @Test
    void parse_missingDate_exceptionThrown() {
        assertThrows(DobbyException.class, () -> DateTimeUtil.parse(""));
    }

    @Test
    void parse_extraDateTimePart_exceptionThrown() {
        assertThrows(DobbyException.class, () -> DateTimeUtil.parse("2019-12-03 0900 extra"));
    }

    @Test
    void formatForDisplay_dateOnly_returnsReadableDate() {
        String result = DateTimeUtil.formatForDisplay(LocalDateTime.of(2019, 12, 3, 9, 5), false);

        assertEquals("Dec 03 2019", result);
    }

    @Test
    void formatForDisplay_dateAndTime_returnsReadableDateAndTime() {
        String result = DateTimeUtil.formatForDisplay(LocalDateTime.of(2019, 12, 3, 9, 5), true);

        assertEquals("Dec 03 2019, 09:05", result);
    }

    @Test
    void formatForStorage_dateOnly_returnsIsoDate() {
        String result = DateTimeUtil.formatForStorage(LocalDateTime.of(2019, 12, 3, 9, 5), false);

        assertEquals("2019-12-03", result);
    }

    @Test
    void formatForStorage_dateAndTime_returnsIsoDateAndCompactTime() {
        String result = DateTimeUtil.formatForStorage(LocalDateTime.of(2019, 12, 3, 9, 5), true);

        assertEquals("2019-12-03 0905", result);
    }
}
