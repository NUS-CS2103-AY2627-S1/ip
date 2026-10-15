package carlo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CarloDateTime}.
 */
class CarloDateTimeTest {

    // ---- parseDate(String) ----

    @Test
    void parseDate_isoFormat_returnsCorrectDate() {
        assertEquals(LocalDate.of(2019, 12, 2), CarloDateTime.parseDate("2019-12-02"));
    }

    @Test
    void parseDate_isoFormatSingleDigitMonthAndDay_returnsCorrectDate() {
        assertEquals(LocalDate.of(2019, 2, 5), CarloDateTime.parseDate("2019-2-5"));
    }

    @Test
    void parseDate_slashFormat_returnsCorrectDate() {
        // d/M/yyyy: day 2, month 12, year 2019
        assertEquals(LocalDate.of(2019, 12, 2), CarloDateTime.parseDate("2/12/2019"));
    }

    @Test
    void parseDate_today_returnsCurrentDate() {
        assertEquals(LocalDate.now(), CarloDateTime.parseDate("today"));
    }

    @Test
    void parseDate_todayMixedCase_returnsCurrentDate() {
        assertEquals(LocalDate.now(), CarloDateTime.parseDate("ToDaY"));
    }

    @Test
    void parseDate_tomorrow_returnsNextDay() {
        assertEquals(LocalDate.now().plusDays(1), CarloDateTime.parseDate("tomorrow"));
    }

    @Test
    void parseDate_yesterday_returnsPreviousDay() {
        assertEquals(LocalDate.now().minusDays(1), CarloDateTime.parseDate("yesterday"));
    }

    @Test
    void parseDate_surroundingWhitespace_trimmedAndParsed() {
        assertEquals(LocalDate.of(2019, 12, 2), CarloDateTime.parseDate("  2019-12-02  "));
    }

    @Test
    void parseDate_invalidCalendarDate_throwsDateTimeParseException() {
        // Month 13 and day 40 do not exist
        assertThrows(DateTimeParseException.class, () -> CarloDateTime.parseDate("2019-13-40"));
    }

    @Test
    void parseDate_unrecognisedFormat_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> CarloDateTime.parseDate("December 2nd 2019"));
    }

    @Test
    void parseDate_emptyString_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> CarloDateTime.parseDate(""));
    }

    @Test
    void parseDate_blankString_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> CarloDateTime.parseDate("   "));
    }

    // ---- parse(String) ----

    @Test
    void parse_dateOnly_isParsedAndDisplaysDateOnly() {
        CarloDateTime dateTime = CarloDateTime.parse("2019-12-02");
        assertTrue(dateTime.isParsed());
        assertEquals("Dec 2 2019", dateTime.toDisplayString());
    }

    @Test
    void parse_dateAndTime_isParsedAndDisplaysDateAndTime() {
        CarloDateTime dateTime = CarloDateTime.parse("2019-12-02 1800");
        assertTrue(dateTime.isParsed());
        assertEquals(LocalDate.of(2019, 12, 2), dateTime.getDateTime().toLocalDate());
        assertEquals(18, dateTime.getDateTime().getHour());
    }

    @Test
    void parse_slashFormatWithTime_isParsedCorrectly() {
        CarloDateTime dateTime = CarloDateTime.parse("2/12/2019 0930");
        assertTrue(dateTime.isParsed());
        assertEquals(LocalDate.of(2019, 12, 2), dateTime.getDateTime().toLocalDate());
        assertEquals(9, dateTime.getDateTime().getHour());
        assertEquals(30, dateTime.getDateTime().getMinute());
    }

    @Test
    void parse_unrecognisedText_isNotParsedAndKeepsOriginalText() {
        CarloDateTime dateTime = CarloDateTime.parse("someday");
        assertFalse(dateTime.isParsed());
        assertEquals("someday", dateTime.toDisplayString());
        assertEquals(null, dateTime.getDateTime());
    }

    // ---- toFileFormat() ----

    @Test
    void toFileFormat_dateOnly_returnsIsoDate() {
        assertEquals("2019-12-02", CarloDateTime.parse("2019-12-02").toFileFormat());
    }

    @Test
    void toFileFormat_dateAndTime_returnsIsoDateWithTime() {
        assertEquals("2019-12-02 1800", CarloDateTime.parse("2019-12-02 1800").toFileFormat());
    }

    @Test
    void toFileFormat_unparsedText_returnsOriginalText() {
        assertEquals("someday", CarloDateTime.parse("someday").toFileFormat());
    }

    // ---- isOnDate(LocalDate) ----

    @Test
    void isOnDate_matchingDate_returnsTrue() {
        assertTrue(CarloDateTime.parse("2019-12-02").isOnDate(LocalDate.of(2019, 12, 2)));
    }

    @Test
    void isOnDate_differentDate_returnsFalse() {
        assertFalse(CarloDateTime.parse("2019-12-02").isOnDate(LocalDate.of(2019, 12, 3)));
    }

    @Test
    void isOnDate_unparsedValue_returnsFalse() {
        assertFalse(CarloDateTime.parse("someday").isOnDate(LocalDate.of(2019, 12, 2)));
    }
}
