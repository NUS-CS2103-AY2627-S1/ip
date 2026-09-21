package maggigorengayam.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import maggigorengayam.util.DateTimeUtil.ParsedDateTime;

public class DateTimeUtilTest {

    @Test
    public void parse_dateOnly_returnsDateWithNullTime() {
        ParsedDateTime result = DateTimeUtil.parse("2019-12-02");

        assertEquals(LocalDate.of(2019, 12, 2), result.date);
        assertNull(result.time);
    }

    @Test
    public void parse_dateWithTime_returnsDateAndTime() {
        ParsedDateTime result = DateTimeUtil.parse("2019-12-02 1800");

        assertEquals(LocalDate.of(2019, 12, 2), result.date);
        assertEquals(LocalTime.of(18, 0), result.time);
    }

    @Test
    public void parse_leadingAndTrailingWhitespace_trimmedAndParsed() {
        ParsedDateTime result = DateTimeUtil.parse("  2019-12-02  ");

        assertEquals(LocalDate.of(2019, 12, 2), result.date);
        assertNull(result.time);
    }

    @Test
    public void parse_extraInternalWhitespaceBetweenDateAndTime_trimmedAndParsed() {
        ParsedDateTime result = DateTimeUtil.parse("2019-12-02  1800");

        assertEquals(LocalDate.of(2019, 12, 2), result.date);
        assertEquals(LocalTime.of(18, 0), result.time);
    }

    /**
     * INPUT_DATE_FORMAT uses ResolverStyle.STRICT (rather than the default
     * SMART, which would silently clamp an out-of-range day to the last
     * valid day of the month), so a non-existent calendar date like
     * "2019-02-30" (February only has 28/29 days) is rejected rather than
     * silently becoming a different date the user never typed.
     */
    @Test
    public void parse_dayOverflowInMonth_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("2019-02-30"));
    }

    @Test
    public void parse_february29OnNonLeapYear_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("2019-02-29"));
    }

    @Test
    public void parse_february29OnLeapYear_parsesSuccessfully() {
        ParsedDateTime result = DateTimeUtil.parse("2020-02-29");

        assertEquals(LocalDate.of(2020, 2, 29), result.date);
        assertNull(result.time);
    }

    @Test
    public void parse_monthOutOfRange_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("2019-13-01"));
    }

    @Test
    public void parse_hourOutOfRange_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("2019-12-02 2500"));
    }

    @Test
    public void parse_nonNumericText_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("abcd"));
    }

    @Test
    public void parse_emptyString_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse(""));
    }

    @Test
    public void parse_unpaddedMonthAndDay_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("2019-2-2"));
    }

    @Test
    public void parse_missingSpaceSeparatorBeforeTime_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("2019-12-02T1800"));
    }

    @Test
    public void parse_timeShorterThanFourDigits_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("2019-12-02 900"));
    }
}
