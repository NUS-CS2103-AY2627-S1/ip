package myriad.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import myriad.MyriadException;

/**
 * Tests for {@link TaskDateTime}, the class that decides which date/time
 * strings Myriad accepts, how they are written to the save file, and how
 * they are shown to the user.
 * <p>
 * The display format always uses English month names. Gradle runs the tests
 * with a Chinese default locale (see {@code build.gradle}), so the
 * {@code toString()} tests also prove that the display does not follow the
 * language of the machine.
 */
public class TaskDateTimeTest {

    /**
     * Fails the test unless saving each input and re-parsing what was saved
     * yields the same text again — the invariant Storage relies on.
     */
    private static void assertRoundTripsUnchanged(String... inputs) throws MyriadException {
        for (String input : inputs) {
            String saved = TaskDateTime.parse(input).toSaveFormat();
            String resaved = TaskDateTime.parse(saved).toSaveFormat();
            assertEquals(saved, resaved, "round-trip changed the value for input: " + input);
        }
    }

    // ---------------------------------------------------------------
    // parse: accepted formats
    // ---------------------------------------------------------------

    @Test
    public void parse_isoDateTime_parsedWithTime() throws MyriadException {
        assertEquals("2019-12-02T18:00", TaskDateTime.parse("2019-12-02T18:00").toSaveFormat());
    }

    @Test
    public void parse_isoDateSpaceCompactTime_parsedWithTime() throws MyriadException {
        assertEquals("2019-12-02T18:00", TaskDateTime.parse("2019-12-02 1800").toSaveFormat());
    }

    @Test
    public void parse_isoDateSpaceColonTime_parsedWithTime() throws MyriadException {
        assertEquals("2019-12-02T18:00", TaskDateTime.parse("2019-12-02 18:00").toSaveFormat());
    }

    @Test
    public void parse_slashDateCompactTime_parsedWithTime() throws MyriadException {
        assertEquals("2019-12-02T18:00", TaskDateTime.parse("2/12/2019 1800").toSaveFormat());
    }

    @Test
    public void parse_slashDateColonTime_parsedWithTime() throws MyriadException {
        assertEquals("2019-12-02T18:00", TaskDateTime.parse("2/12/2019 18:00").toSaveFormat());
    }

    @Test
    public void parse_isoDateOnly_parsedWithoutTime() throws MyriadException {
        assertEquals("2019-12-02", TaskDateTime.parse("2019-12-02").toSaveFormat());
    }

    @Test
    public void parse_slashDateOnly_parsedWithoutTime() throws MyriadException {
        assertEquals("2019-12-02", TaskDateTime.parse("2/12/2019").toSaveFormat());
    }

    @Test
    public void parse_slashDateIsDayThenMonth_notMonthThenDay() throws MyriadException {
        // "2/12/2019" must be 2 December, not 12 February. The pattern is
        // d/M/yyyy, so a day above 12 is the unambiguous proof.
        assertEquals("2019-01-31", TaskDateTime.parse("31/1/2019").toSaveFormat());
    }

    @Test
    public void parse_slashDateWithLeadingZeros_accepted() throws MyriadException {
        // The pattern uses single "d"/"M", which tolerates zero-padding too.
        assertEquals("2019-12-02", TaskDateTime.parse("02/12/2019").toSaveFormat());
    }

    @Test
    public void parse_isoDateTimeWithSeconds_secondsPreserved() throws MyriadException {
        // ISO_LOCAL_DATE_TIME accepts seconds, and toSaveFormat() keeps them,
        // so the save file can hold more precision than toString() displays.
        assertEquals("2019-12-02T18:00:30", TaskDateTime.parse("2019-12-02T18:00:30").toSaveFormat());
    }

    // ---------------------------------------------------------------
    // parse: the date-only vs date+time ordering guarantee
    // ---------------------------------------------------------------

    @Test
    public void parse_dateOnlyInput_notMisreadAsDateTime() throws MyriadException {
        // The date+time formats are all tried first. A bare date must fall
        // through all of them rather than being defaulted to midnight, which
        // is what keeps "no time given" distinguishable from "midnight".
        String saved = TaskDateTime.parse("2019-12-02").toSaveFormat();
        assertTrue(!saved.contains("T"), "date-only value must not gain a time component");
    }

    @Test
    public void parse_midnightAndDateOnly_produceDifferentValues() throws MyriadException {
        // Both start at 00:00, but only the date-only one spans the whole day.
        TaskDateTime dateOnly = TaskDateTime.parse("2019-12-02");
        TaskDateTime midnight = TaskDateTime.parse("2019-12-02 0000");
        assertEquals(dateOnly.rangeStart(), midnight.rangeStart());
        assertNotEquals(dateOnly.rangeEnd(), midnight.rangeEnd());
        assertNotEquals(dateOnly.toSaveFormat(), midnight.toSaveFormat());
    }

    // ---------------------------------------------------------------
    // parse: rejected input
    // ---------------------------------------------------------------

    @Test
    public void parse_plainWord_exceptionThrown() {
        assertThrows(MyriadException.class, () -> TaskDateTime.parse("monday"));
    }

    @Test
    public void parse_emptyString_exceptionThrown() {
        assertThrows(MyriadException.class, () -> TaskDateTime.parse(""));
    }

    @Test
    public void parse_twoDigitYear_exceptionThrown() {
        assertThrows(MyriadException.class, () -> TaskDateTime.parse("2/12/19"));
    }

    @Test
    public void parse_monthOutOfRange_exceptionThrown() {
        assertThrows(MyriadException.class, () -> TaskDateTime.parse("2019-13-01"));
    }

    @Test
    public void parse_hourOutOfRange_exceptionThrown() {
        assertThrows(MyriadException.class, () -> TaskDateTime.parse("2019-12-02 2500"));
    }

    @Test
    public void parse_surroundingWhitespace_exceptionThrown() {
        // Callers are expected to strip first (Parser does). Documented here
        // so the precondition is not silently relaxed later.
        assertThrows(MyriadException.class, () -> TaskDateTime.parse(" 2019-12-02 "));
    }

    @Test
    public void parse_isoDateWithUnpaddedFields_exceptionThrown() {
        // ISO_LOCAL_DATE demands yyyy-MM-dd exactly; "2019-2-3" is not it,
        // and no slash format matches either.
        assertThrows(MyriadException.class, () -> TaskDateTime.parse("2019-2-3"));
    }

    @Test
    public void parse_isoImpossibleDayOfMonth_exceptionThrown() {
        assertThrows(MyriadException.class, () -> TaskDateTime.parse("2019-02-30"));
    }

    @Test
    public void parse_unrecognisedInput_messageNamesExampleFormats() {
        MyriadException e = assertThrows(MyriadException.class, () -> TaskDateTime.parse("monday"));
        assertTrue(e.getMessage().contains("monday"), "message should quote the offending input");
        assertTrue(e.getMessage().contains("2019-12-02"), "message should show an accepted example");
    }

    // ---------------------------------------------------------------
    // parse: known limitations, asserted as they currently behave
    // ---------------------------------------------------------------

    @Test
    public void parse_null_nullPointerExceptionThrown() {
        // Known limitation: parse() catches only DateTimeParseException, so a
        // null argument escapes as an NPE rather than a MyriadException. No
        // caller passes null today, so this records the behaviour rather than
        // treating it as a supported input.
        assertThrows(NullPointerException.class, () -> TaskDateTime.parse(null));
    }

    // ---------------------------------------------------------------
    // parse: dates that do not exist are rejected in every format
    // ---------------------------------------------------------------

    @Test
    public void parse_impossibleDates_exceptionThrown() {
        // Every accepted format must reject these rather than quietly moving
        // the date to the end of the month, which is what a lenient formatter
        // would do.
        String[] impossibleDates = {
            "31/2/2019", "29/2/2019", "31/4/2019", "32/1/2019", "0/1/2019", "1/13/2019",
            "30/2/2019 1800", "30/2/2019 18:00",
            "2019-02-30 1800", "2019-02-30 18:00", "2019-02-29T18:00", "2019-04-31"
        };
        for (String raw : impossibleDates) {
            assertThrows(MyriadException.class, () -> TaskDateTime.parse(raw), raw + " should be rejected");
        }
    }

    @Test
    public void parse_impossibleTimes_exceptionThrown() {
        String[] impossibleTimes = {"2019-12-02 2400", "2019-12-02 1860", "2/12/2019 25:00"};
        for (String raw : impossibleTimes) {
            assertThrows(MyriadException.class, () -> TaskDateTime.parse(raw), raw + " should be rejected");
        }
    }

    @Test
    public void parse_feb29InLeapYear_accepted() throws MyriadException {
        // Strict checking must still accept a date that does exist.
        assertEquals("2020-02-29", TaskDateTime.parse("29/2/2020").toSaveFormat());
        assertEquals("2020-02-29T18:00", TaskDateTime.parse("29/2/2020 1800").toSaveFormat());
    }

    // ---------------------------------------------------------------
    // toSaveFormat: the round-trip invariant Storage depends on
    // ---------------------------------------------------------------

    @Test
    public void toSaveFormat_everyAcceptedInput_reparsesToSameValue() throws MyriadException {
        // Storage writes toSaveFormat() and reads it back with parse() on the
        // next launch, so this must hold for every form a user can type --
        // otherwise tasks would change or vanish across restarts.
        assertRoundTripsUnchanged(
                "2019-12-02T18:00",
                "2019-12-02 1800",
                "2019-12-02 18:00",
                "2/12/2019 1800",
                "2/12/2019 18:00",
                "2019-12-02T18:00:30",
                "2019-12-02 0000",
                "2019-12-02",
                "2/12/2019");
    }

    // ---------------------------------------------------------------
    // rangeStart / rangeEnd
    // ---------------------------------------------------------------

    @Test
    public void rangeStartAndEnd_dateOnly_spansWholeDay() throws MyriadException {
        TaskDateTime dateOnly = TaskDateTime.parse("2019-12-02");
        assertEquals(LocalDateTime.parse("2019-12-02T00:00"), dateOnly.rangeStart());
        assertEquals(LocalDateTime.parse("2019-12-02T23:59:59.999999999"), dateOnly.rangeEnd());
    }

    @Test
    public void rangeStartAndEnd_withTime_collapsesToSingleInstant() throws MyriadException {
        TaskDateTime timed = TaskDateTime.parse("2019-12-02 1800");
        assertEquals(LocalDateTime.parse("2019-12-02T18:00"), timed.rangeStart());
        assertEquals(timed.rangeStart(), timed.rangeEnd());
    }

    // ---------------------------------------------------------------
    // rangesOverlap
    // ---------------------------------------------------------------

    @Test
    public void rangesOverlap_touchingEndpoints_returnsTrue() {
        // The interval is closed at both ends, so back-to-back ranges count
        // as overlapping: an event ending at 12:00 is "occurring on" 12:00.
        assertTrue(TaskDateTime.rangesOverlap(
                LocalDateTime.parse("2019-12-02T10:00"),
                LocalDateTime.parse("2019-12-02T12:00"),
                LocalDateTime.parse("2019-12-02T12:00"),
                LocalDateTime.parse("2019-12-02T14:00")));
    }

    @Test
    public void rangesOverlap_oneNanosecondApart_returnsFalse() {
        assertTrue(!TaskDateTime.rangesOverlap(
                LocalDateTime.parse("2019-12-02T10:00"),
                LocalDateTime.parse("2019-12-02T12:00"),
                LocalDateTime.parse("2019-12-02T12:00:00.000000001"),
                LocalDateTime.parse("2019-12-02T14:00")));
    }

    @Test
    public void rangesOverlap_partialOverlap_returnsTrue() {
        assertTrue(TaskDateTime.rangesOverlap(
                LocalDateTime.parse("2019-12-02T10:00"),
                LocalDateTime.parse("2019-12-02T13:00"),
                LocalDateTime.parse("2019-12-02T12:00"),
                LocalDateTime.parse("2019-12-02T14:00")));
    }

    @Test
    public void rangesOverlap_oneRangeContainsOther_returnsTrue() {
        assertTrue(TaskDateTime.rangesOverlap(
                LocalDateTime.parse("2019-12-02T00:00"),
                LocalDateTime.parse("2019-12-02T23:59"),
                LocalDateTime.parse("2019-12-02T12:00"),
                LocalDateTime.parse("2019-12-02T12:30")));
    }

    @Test
    public void rangesOverlap_instantInsideRange_returnsTrue() {
        // A zero-width range (what a timed TaskDateTime produces) inside a
        // wider one -- the shape "show 2019-12-02 1200" actually uses.
        assertTrue(TaskDateTime.rangesOverlap(
                LocalDateTime.parse("2019-12-02T10:00"),
                LocalDateTime.parse("2019-12-02T14:00"),
                LocalDateTime.parse("2019-12-02T12:00"),
                LocalDateTime.parse("2019-12-02T12:00")));
    }

    @Test
    public void rangesOverlap_disjointRanges_returnsFalse() {
        assertTrue(!TaskDateTime.rangesOverlap(
                LocalDateTime.parse("2019-12-02T10:00"),
                LocalDateTime.parse("2019-12-02T11:00"),
                LocalDateTime.parse("2019-12-05T10:00"),
                LocalDateTime.parse("2019-12-05T11:00")));
    }

    @Test
    public void rangesOverlap_argumentsSwapped_sameResult() {
        LocalDateTime aStart = LocalDateTime.parse("2019-12-02T10:00");
        LocalDateTime aEnd = LocalDateTime.parse("2019-12-02T13:00");
        LocalDateTime bStart = LocalDateTime.parse("2019-12-02T12:00");
        LocalDateTime bEnd = LocalDateTime.parse("2019-12-02T14:00");
        assertEquals(
                TaskDateTime.rangesOverlap(aStart, aEnd, bStart, bEnd),
                TaskDateTime.rangesOverlap(bStart, bEnd, aStart, aEnd));
    }

    // ---------------------------------------------------------------
    // toString (display format)
    // ---------------------------------------------------------------

    @Test
    public void toString_dateOnly_omitsTime() throws MyriadException {
        assertEquals("Dec 02 2019", TaskDateTime.parse("2019-12-02").toString());
    }

    @Test
    public void toString_withTime_showsCompactTime() throws MyriadException {
        assertEquals("Dec 02 2019 1800", TaskDateTime.parse("2019-12-02 1800").toString());
    }

    @Test
    public void toString_slashInput_displayedInSameFormatAsIsoInput() throws MyriadException {
        // The input format must not leak into the display.
        assertEquals(
                TaskDateTime.parse("2019-12-02").toString(),
                TaskDateTime.parse("2/12/2019").toString());
    }

    @Test
    public void toString_testJvmLocaleNotEnglish_monthNameStillEnglish() throws MyriadException {
        // build.gradle runs the tests with a Chinese default locale, so this
        // and the other toString tests would fail if the display formatters
        // followed the machine's language instead of always using English.
        assertEquals("zh", Locale.getDefault().getLanguage(), "run the tests through Gradle");
        assertEquals("Sep 20 2026 1400", TaskDateTime.parse("2026-09-20 1400").toString());
    }

    @Test
    public void toString_withSeconds_secondsNotDisplayed() throws MyriadException {
        // Display is lossy where toSaveFormat() is not: the seconds survive a
        // save/load round trip but are never shown to the user.
        assertEquals("Dec 02 2019 1800", TaskDateTime.parse("2019-12-02T18:00:30").toString());
    }

    // ---------------------------------------------------------------
    // equals and hashCode: what makes two dates "the same" for duplicates
    // ---------------------------------------------------------------

    @Test
    public void equals_sameDateTimeTypedDifferently_equal() throws MyriadException {
        TaskDateTime iso = TaskDateTime.parse("2019-12-02 1800");
        TaskDateTime slash = TaskDateTime.parse("2/12/2019 18:00");
        assertEquals(iso, slash);
        assertEquals(iso.hashCode(), slash.hashCode());
    }

    @Test
    public void equals_differentTime_notEqual() throws MyriadException {
        assertNotEquals(TaskDateTime.parse("2019-12-02 1800"), TaskDateTime.parse("2019-12-02 1801"));
    }

    @Test
    public void equals_dateOnlyAndMidnight_notEqual() throws MyriadException {
        // "No time given" is different information from "at 00:00".
        assertNotEquals(TaskDateTime.parse("2019-12-02"), TaskDateTime.parse("2019-12-02 0000"));
    }

    @Test
    public void equals_sameObject_equal() throws MyriadException {
        TaskDateTime date = TaskDateTime.parse("2019-12-02");
        assertTrue(date.equals(date));
    }

    @Test
    public void equals_nullOrOtherType_notEqual() throws MyriadException {
        // equals is called on the TaskDateTime directly: assertNotEquals(null,
        // date) would compare from the null side and never reach this method.
        TaskDateTime date = TaskDateTime.parse("2019-12-02");
        assertFalse(date.equals(null));
        assertFalse(date.equals("2019-12-02"));
    }
}
