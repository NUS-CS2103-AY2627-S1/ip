package meowmeow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import meowmeow.MeowmeowException;

/**
 * Tests for every {@link TaskDateTime} method with behaviour worth
 * checking:
 *
 * <ul>
 *   <li>{@link TaskDateTime#parse(String)} - the accepted formats, and the
 *       many ways an input can be rejected;</li>
 *   <li>{@link TaskDateTime#toString()} - the on-screen form, including the
 *       date-only vs date-and-time split and the am/pm lower-casing;</li>
 *   <li>{@link TaskDateTime#toFileString()} - the save-file form, and the
 *       fact that it round-trips back through {@code parse};</li>
 *   <li>{@link TaskDateTime#toDateString()} - the date part on its own;</li>
 *   <li>{@link TaskDateTime#isNotAfter(TaskDateTime)} - the ordering used to
 *       check an event's start is not after its end;</li>
 *   <li>{@link TaskDateTime#getDate()} - that it exposes just the date.</li>
 * </ul>
 *
 * <p>These methods unit-test well: {@code parse} is {@code static} and the
 * rest are pure (no console, file or clock), so every case is a plain
 * input/expected-output pair.
 *
 * <p>The private constructor and the private {@code strict(...)} helper are
 * not tested directly - they have no branching of their own and are
 * exercised through the public methods above.
 *
 * <p>Test methods are named {@code feature_scenario_expectedBehaviour} as
 * suggested by the course when the full sentence would be unwieldy.
 */
public class TaskDateTimeTest {

    // ================================================================
    // parse: accepted formats - date with a time-of-day
    // ================================================================

    @Test
    public void parse_dayMonthYearWithTime_timeShownInOutput() throws MeowmeowException {
        TaskDateTime parsed = TaskDateTime.parse("2/12/2019 1800");

        assertEquals(LocalDate.of(2019, 12, 2), parsed.getDate());
        // Display form lower-cases the am/pm marker (course's expected form).
        assertEquals("Dec 2 2019, 6:00 pm", parsed.toString());
        // File form is itself an accepted input format, so it round-trips.
        assertEquals("2019-12-02 1800", parsed.toFileString());
    }

    @Test
    public void parse_isoDateWithTime_parsedSameAsSlashForm() throws MeowmeowException {
        TaskDateTime parsed = TaskDateTime.parse("2019-12-02 1800");

        assertEquals(LocalDate.of(2019, 12, 2), parsed.getDate());
        assertEquals("Dec 2 2019, 6:00 pm", parsed.toString());
        assertEquals("2019-12-02 1800", parsed.toFileString());
    }

    @Test
    public void parse_midnightTime_treatedAsTimePresentNotAbsent() throws MeowmeowException {
        TaskDateTime parsed = TaskDateTime.parse("2/12/2019 0000");

        // A time of 00:00 was typed, so it must still be shown - not dropped
        // as if only a date had been given.
        assertEquals("Dec 2 2019, 12:00 am", parsed.toString());
        assertEquals("2019-12-02 0000", parsed.toFileString());
    }

    // ================================================================
    // parse: accepted formats - date only
    // ================================================================

    @Test
    public void parse_dayMonthYearNoTime_noTimeInOutput() throws MeowmeowException {
        TaskDateTime parsed = TaskDateTime.parse("2/12/2019");

        assertEquals(LocalDate.of(2019, 12, 2), parsed.getDate());
        assertEquals("Dec 2 2019", parsed.toString());
        assertEquals("2019-12-02", parsed.toFileString());
    }

    @Test
    public void parse_isoDateNoTime_noTimeInOutput() throws MeowmeowException {
        TaskDateTime parsed = TaskDateTime.parse("2019-12-02");

        assertEquals(LocalDate.of(2019, 12, 2), parsed.getDate());
        assertEquals("Dec 2 2019", parsed.toString());
        assertEquals("2019-12-02", parsed.toFileString());
    }

    @Test
    public void parse_singleDigitDayAndMonth_accepted() throws MeowmeowException {
        TaskDateTime parsed = TaskDateTime.parse("1/2/2019");

        assertEquals(LocalDate.of(2019, 2, 1), parsed.getDate());
        assertEquals("2019-02-01", parsed.toFileString());
    }

    // ================================================================
    // parse: whitespace handling
    // ================================================================

    @Test
    public void parse_surroundingWhitespace_ignored() throws MeowmeowException {
        TaskDateTime parsed = TaskDateTime.parse("   2/12/2019 1800   ");

        assertEquals("2019-12-02 1800", parsed.toFileString());
    }

    // ================================================================
    // parse: rejected input - unrecognised format
    // ================================================================

    @Test
    public void parse_emptyString_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse(""));
    }

    @Test
    public void parse_blankString_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse("   "));
    }

    @Test
    public void parse_nonDateText_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse("tomorrow"));
    }

    @Test
    public void parse_wrongSeparator_exceptionThrown() {
        // Dots are not one of the accepted separators ("/" or "-").
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse("2.12.2019"));
    }

    @Test
    public void parse_twoDigitYear_exceptionThrown() {
        // The patterns require a 4-digit year ("uuuu").
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse("2/12/19"));
    }

    @Test
    public void parse_timeWithColon_exceptionThrown() {
        // The accepted time form is "HHmm" (1800), not "18:00".
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse("2/12/2019 18:00"));
    }

    @Test
    public void parse_trailingGarbageAfterValidDate_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse("2/12/2019 pizza"));
    }

    @Test
    public void parse_mixedSeparatorsInDate_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse("2019-12/02"));
    }

    // ================================================================
    // parse: rejected input - format fits but the date/time is not real
    // ================================================================

    @Test
    public void parse_impossibleCalendarDate_exceptionThrown() {
        // 2019 is not a leap year, so 29 Feb does not exist. The STRICT
        // resolver must reject this rather than snap it to 28 Feb / 1 Mar.
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse("29/2/2019"));
    }

    @Test
    public void parse_monthOutOfRange_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse("1/13/2019"));
    }

    @Test
    public void parse_dayOutOfRangeForMonth_exceptionThrown() {
        // April has 30 days.
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse("31/4/2019"));
    }

    @Test
    public void parse_dayZero_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse("0/1/2019"));
    }

    @Test
    public void parse_hourOutOfRange_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse("2/12/2019 2500"));
    }

    @Test
    public void parse_minuteOutOfRange_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> TaskDateTime.parse("2/12/2019 1860"));
    }

    // ================================================================
    // parse: leap year - the valid counterpart of the impossible-date case
    // ================================================================

    @Test
    public void parse_leapDayInLeapYear_accepted() throws MeowmeowException {
        TaskDateTime parsed = TaskDateTime.parse("29/2/2020");

        assertEquals(LocalDate.of(2020, 2, 29), parsed.getDate());
        assertEquals("2020-02-29", parsed.toFileString());
    }

    // ================================================================
    // toString: on-screen form
    // ================================================================

    @Test
    public void toString_dateOnly_noCommaAndNoTime() throws MeowmeowException {
        assertEquals("Oct 15 2019", TaskDateTime.parse("2019-10-15").toString());
    }

    @Test
    public void toString_afternoonTime_twelveHourClockWithLowerCasePm() throws MeowmeowException {
        assertEquals("Dec 2 2019, 4:00 pm", TaskDateTime.parse("2/12/2019 1600").toString());
    }

    @Test
    public void toString_morningTime_lowerCaseAm() throws MeowmeowException {
        assertEquals("Dec 2 2019, 9:05 am", TaskDateTime.parse("2/12/2019 0905").toString());
    }

    @Test
    public void toString_noon_shownAsTwelvePm() throws MeowmeowException {
        assertEquals("Dec 2 2019, 12:00 pm", TaskDateTime.parse("2/12/2019 1200").toString());
    }

    @Test
    public void toString_dayNotZeroPadded() throws MeowmeowException {
        // DISPLAY_DATE uses "d", not "dd", so a single-digit day has no
        // leading zero on screen (unlike the file form).
        assertEquals("Jan 5 2019", TaskDateTime.parse("5/1/2019").toString());
    }

    // ================================================================
    // toFileString: save-file form, and its round-trip guarantee
    // ================================================================

    @Test
    public void toFileString_singleDigitDayAndMonth_zeroPadded() throws MeowmeowException {
        assertEquals("2019-02-01", TaskDateTime.parse("1/2/2019").toFileString());
    }

    @Test
    public void toFileString_timeBeforeTen_zeroPaddedToFourDigits() throws MeowmeowException {
        assertEquals("2019-02-01 0905", TaskDateTime.parse("1/2/2019 0905").toFileString());
    }

    @Test
    public void toFileString_dateOnlyValue_roundTripsThroughParse() throws MeowmeowException {
        TaskDateTime original = TaskDateTime.parse("2/12/2019");
        TaskDateTime reloaded = TaskDateTime.parse(original.toFileString());

        assertEquals(original.toFileString(), reloaded.toFileString());
        assertEquals(original.toString(), reloaded.toString());
    }

    @Test
    public void toFileString_dateAndTimeValue_roundTripsThroughParse() throws MeowmeowException {
        TaskDateTime original = TaskDateTime.parse("2/12/2019 1800");
        TaskDateTime reloaded = TaskDateTime.parse(original.toFileString());

        assertEquals(original.toFileString(), reloaded.toFileString());
        assertEquals(original.toString(), reloaded.toString());
    }

    // ================================================================
    // toDateString: date part only, regardless of whether a time was given
    // ================================================================

    @Test
    public void toDateString_valueWithTime_returnsDateWithoutTime() throws MeowmeowException {
        assertEquals("Dec 2 2019", TaskDateTime.parse("2/12/2019 1800").toDateString());
    }

    @Test
    public void toDateString_dateOnlyValue_sameAsToString() throws MeowmeowException {
        TaskDateTime dateOnly = TaskDateTime.parse("2/12/2019");

        assertEquals("Dec 2 2019", dateOnly.toDateString());
        assertEquals(dateOnly.toString(), dateOnly.toDateString());
    }

    // ================================================================
    // getDate: exposes just the calendar date
    // ================================================================

    @Test
    public void getDate_valueWithTime_returnsDatePartOnly() throws MeowmeowException {
        assertEquals(LocalDate.of(2019, 12, 2), TaskDateTime.parse("2/12/2019 1800").getDate());
    }

    // ================================================================
    // isNotAfter: ordering used to validate an event's start vs end
    // ================================================================

    @Test
    public void isNotAfter_earlierTimeSameDay_true() throws MeowmeowException {
        TaskDateTime earlier = TaskDateTime.parse("2/12/2019 1400");
        TaskDateTime later = TaskDateTime.parse("2/12/2019 1600");

        assertTrue(earlier.isNotAfter(later));
    }

    @Test
    public void isNotAfter_laterTimeSameDay_false() throws MeowmeowException {
        TaskDateTime earlier = TaskDateTime.parse("2/12/2019 1400");
        TaskDateTime later = TaskDateTime.parse("2/12/2019 1600");

        assertFalse(later.isNotAfter(earlier));
    }

    @Test
    public void isNotAfter_sameInstant_true() throws MeowmeowException {
        TaskDateTime one = TaskDateTime.parse("2/12/2019 1400");
        TaskDateTime same = TaskDateTime.parse("2/12/2019 1400");

        assertTrue(one.isNotAfter(same));
    }

    @Test
    public void isNotAfter_earlierDay_dayWinsOverClockTime() throws MeowmeowException {
        // Late on the 1st vs early on the 2nd: the earlier day is not after
        // the later one even though its clock time is bigger.
        TaskDateTime lateFirst = TaskDateTime.parse("1/12/2019 2300");
        TaskDateTime earlySecond = TaskDateTime.parse("2/12/2019 0100");

        assertTrue(lateFirst.isNotAfter(earlySecond));
        assertFalse(earlySecond.isNotAfter(lateFirst));
    }

    @Test
    public void isNotAfter_bothDateOnlySameDay_true() throws MeowmeowException {
        TaskDateTime one = TaskDateTime.parse("2/12/2019");
        TaskDateTime same = TaskDateTime.parse("2019-12-02");

        // Both missing times count as 00:00, so neither is after the other.
        assertTrue(one.isNotAfter(same));
        assertTrue(same.isNotAfter(one));
    }

    @Test
    public void isNotAfter_bothDateOnlyDifferentDays_ordersByDate() throws MeowmeowException {
        TaskDateTime firstDay = TaskDateTime.parse("1/12/2019");
        TaskDateTime secondDay = TaskDateTime.parse("2/12/2019");

        assertTrue(firstDay.isNotAfter(secondDay));
        assertFalse(secondDay.isNotAfter(firstDay));
    }

    @Test
    public void isNotAfter_missingTimeCountsAsStartOfDay() throws MeowmeowException {
        TaskDateTime dateOnly = TaskDateTime.parse("2/12/2019");
        TaskDateTime sameDayWithTime = TaskDateTime.parse("2/12/2019 0900");

        // A missing time is treated as 00:00, so the date-only value is not
        // after the 09:00 one, but the 09:00 one IS after the date-only one.
        assertTrue(dateOnly.isNotAfter(sameDayWithTime));
        assertFalse(sameDayWithTime.isNotAfter(dateOnly));
    }
}
