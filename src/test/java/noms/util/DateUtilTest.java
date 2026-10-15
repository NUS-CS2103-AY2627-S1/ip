package noms.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import noms.exception.InvalidDateException;

/**
 * Tests {@link DateUtil}, covering both of its public methods:
 * {@link DateUtil#parse(String)} and {@link DateUtil#format(LocalDate)}.
 *
 * Both are good JUnit targets because they are pure static methods with
 * deterministic output. {@code parse} has a well-defined failure mode (valid
 * ISO dates return a {@link LocalDate}, anything else throws
 * {@link InvalidDateException}), while {@code format} performs non-trivial
 * rendering (two-digit day padding and a month abbreviation) that is worth
 * pinning down. The tests cover the accepted format and the main rejection
 * paths for {@code parse}, and the notable rendering cases for {@code format}.
 */
public class DateUtilTest {

    // --- parse: valid input returns the matching date ---

    @Test
    public void parse_validIsoDate_returnsMatchingDate() throws InvalidDateException {
        assertEquals(LocalDate.of(2019, 10, 15), DateUtil.parse("2019-10-15"));
    }

    @Test
    public void parse_leapDayInLeapYear_returnsMatchingDate() throws InvalidDateException {
        // 2020 is a leap year, so Feb 29 exists and must be accepted.
        assertEquals(LocalDate.of(2020, 2, 29), DateUtil.parse("2020-02-29"));
    }

    // --- parse: invalid input throws InvalidDateException ---

    @Test
    public void parse_leapDayInNonLeapYear_throwsInvalidDateException() {
        // 2019 is not a leap year, so Feb 29 is not a real calendar date.
        assertThrows(InvalidDateException.class, () -> DateUtil.parse("2019-02-29"));
    }

    @Test
    public void parse_nonExistentCalendarDate_throwsInvalidDateException() {
        // April has 30 days, so 2019-04-31 is well-formed but not a real date.
        assertThrows(InvalidDateException.class, () -> DateUtil.parse("2019-04-31"));
    }

    @Test
    public void parse_monthOutOfRange_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class, () -> DateUtil.parse("2019-13-01"));
    }

    @Test
    public void parse_nonIsoFormatWithSlashes_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class, () -> DateUtil.parse("2019/10/15"));
    }

    @Test
    public void parse_dayMonthYearOrder_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class, () -> DateUtil.parse("15-10-2019"));
    }

    @Test
    public void parse_missingZeroPadding_throwsInvalidDateException() {
        // ISO format requires two-digit month and day, so "2019-1-5" is rejected.
        assertThrows(InvalidDateException.class, () -> DateUtil.parse("2019-1-5"));
    }

    @Test
    public void parse_nonDateText_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class, () -> DateUtil.parse("tomorrow"));
    }

    @Test
    public void parse_emptyString_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class, () -> DateUtil.parse(""));
    }

    // --- format: renders a date as "<3-letter month> dd yyyy" ---

    @Test
    public void format_typicalDate_returnsFriendlyPattern() {
        assertEquals("Oct 15 2019", DateUtil.format(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void format_singleDigitDay_padsDayToTwoDigits() {
        // "dd" always renders two digits, so the 5th becomes "05", not "5".
        assertEquals("Jan 05 2019", DateUtil.format(LocalDate.of(2019, 1, 5)));
    }

    @Test
    public void format_endOfYear_returnsFriendlyPattern() {
        assertEquals("Dec 31 2019", DateUtil.format(LocalDate.of(2019, 12, 31)));
    }

    @Test
    public void format_september_usesFixedThreeLetterAbbreviation() {
        // Pinned to DateUtil's own MONTH_ABBREVIATIONS map, not the JDK's locale
        // data, whose short form for September varies between JDK builds ("Sep"
        // vs "Sept") and previously made this test fail only on some machines/CI.
        assertEquals("Sep 01 2019", DateUtil.format(LocalDate.of(2019, 9, 1)));
    }

    @Test
    public void format_parsedDate_roundTripsToInputComponents() throws InvalidDateException {
        // parse then format keeps the same calendar date, in the display form.
        assertEquals("Jun 06 2019", DateUtil.format(DateUtil.parse("2019-06-06")));
    }
}
