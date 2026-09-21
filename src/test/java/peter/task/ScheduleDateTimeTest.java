package peter.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for every public method of {@link ScheduleDateTime}.
 *
 * <p>All four methods are pure functions: they map an input value to a result
 * without touching the console or the save file, so every case below can be
 * checked by comparing the returned value (or the thrown exception) against an
 * expected result. The private constructor is deliberately untested, since it
 * only prevents instantiation of this utility class.
 */
public class ScheduleDateTimeTest {

    // =====================================================================
    // parseUserInput(String)
    // =====================================================================

    // --- Accepted format 1: ISO date only (e.g. 2019-12-02) ---

    @Test
    public void parseUserInput_isoDate_startOfDayReturned() {
        // A date without a time must default to midnight, per the Javadoc.
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                ScheduleDateTime.parseUserInput("2019-12-02"));
    }

    @Test
    public void parseUserInput_isoDateOnLeapDay_startOfDayReturned() {
        // 2020 is a leap year, so Feb 29 is a real date and must be accepted.
        assertEquals(LocalDateTime.of(2020, 2, 29, 0, 0),
                ScheduleDateTime.parseUserInput("2020-02-29"));
    }

    @Test
    public void parseUserInput_isoDateWithoutPaddedFields_exceptionThrown() {
        // ISO_LOCAL_DATE demands two-digit month and day, and "2019-2-3" is not
        // a valid "d/M/uuuu HHmm" value either, so both attempts fail.
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseUserInput("2019-2-3"));
    }

    @Test
    public void parseUserInput_isoDateWithImpossibleDay_exceptionThrown() {
        // 2019 is not a leap year; the strict ISO parser rejects Feb 29.
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseUserInput("2019-02-29"));
    }

    // --- Accepted format 2: day/month/year plus 24-hour time (e.g. 2/12/2019 1800) ---

    @Test
    public void parseUserInput_dateTimeWithSingleDigitFields_valueReturned() {
        // The "d/M" pattern letters accept one or two digits.
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                ScheduleDateTime.parseUserInput("2/12/2019 1800"));
    }

    @Test
    public void parseUserInput_dateTimeWithPaddedFields_valueReturned() {
        // Zero-padded fields must parse identically to the unpadded form.
        assertEquals(LocalDateTime.of(2019, 1, 5, 9, 30),
                ScheduleDateTime.parseUserInput("05/01/2019 0930"));
    }

    @Test
    public void parseUserInput_dateTimeAtMidnight_valueReturned() {
        // "0000" is the lowest valid time and must not be confused with a failure.
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                ScheduleDateTime.parseUserInput("2/12/2019 0000"));
    }

    @Test
    public void parseUserInput_dateTimeAtLastMinuteOfDay_valueReturned() {
        // "2359" is the highest valid time; this pins the upper boundary.
        assertEquals(LocalDateTime.of(2019, 12, 2, 23, 59),
                ScheduleDateTime.parseUserInput("2/12/2019 2359"));
    }

    @Test
    public void parseUserInput_dateTimeWithHourOutOfRange_exceptionThrown() {
        // "2400" rolls past the end of the day and is rejected by HHmm.
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseUserInput("2/12/2019 2400"));
    }

    @Test
    public void parseUserInput_dateTimeWithMinuteOutOfRange_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseUserInput("2/12/2019 1860"));
    }

    @Test
    public void parseUserInput_dateTimeWithImpossibleDay_exceptionThrown() {
        // STRICT resolution means an out-of-range day is an error rather than
        // being silently shifted to Mar 1.
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseUserInput("29/2/2019 1800"));
    }

    @Test
    public void parseUserInput_dateTimeWithImpossibleMonth_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseUserInput("2/13/2019 1800"));
    }

    @Test
    public void parseUserInput_dateTimeMissingTime_exceptionThrown() {
        // "2/12/2019" matches neither format: it is not ISO, and the time is required.
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseUserInput("2/12/2019"));
    }

    // --- Malformed input ---

    @Test
    public void parseUserInput_untrimmedValue_exceptionThrown() {
        // The method does not trim, so surrounding spaces are a parse failure.
        // Callers are expected to trim before calling.
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseUserInput(" 2019-12-02 "));
    }

    @Test
    public void parseUserInput_emptyString_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseUserInput(""));
    }

    @Test
    public void parseUserInput_nonDateText_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseUserInput("tomorrow"));
    }

    @Test
    public void parseUserInput_nullValue_exceptionThrown() {
        // LocalDate.parse rejects null with a NullPointerException, which is not
        // a DateTimeParseException, so the contract differs for null.
        assertThrows(NullPointerException.class, () ->
                ScheduleDateTime.parseUserInput(null));
    }

    // =====================================================================
    // parseStoredValue(String)
    // =====================================================================
    //
    // This reads the save file rather than user input, so it accepts the ISO
    // date-time that format-independent storage writes, plus the date-only
    // records left behind by earlier versions of the app.

    @Test
    public void parseStoredValue_isoDateTime_valueReturned() {
        // The normal case: what Storage writes today.
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                ScheduleDateTime.parseStoredValue("2019-12-02T18:00"));
    }

    @Test
    public void parseStoredValue_isoDateTimeAtMidnight_valueReturned() {
        // LocalDateTime.toString() omits a zero time, so a midnight task is
        // stored as "2019-12-02T00:00" and must survive the round trip.
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                ScheduleDateTime.parseStoredValue("2019-12-02T00:00"));
    }

    @Test
    public void parseStoredValue_isoDateTimeWithSeconds_valueReturned() {
        // ISO_LOCAL_DATE_TIME allows an optional seconds field.
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0, 30),
                ScheduleDateTime.parseStoredValue("2019-12-02T18:00:30"));
    }

    @Test
    public void parseStoredValue_legacyDateOnly_startOfDayReturned() {
        // Backwards compatibility: an older save file holds just a date, which
        // must be read back as midnight instead of crashing the app on load.
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                ScheduleDateTime.parseStoredValue("2019-12-02"));
    }

    @Test
    public void parseStoredValue_userInputFormat_exceptionThrown() {
        // The user-facing "d/M/uuuu HHmm" format is never written to storage,
        // so it is correctly treated as a corrupt record.
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseStoredValue("2/12/2019 1800"));
    }

    @Test
    public void parseStoredValue_impossibleDate_exceptionThrown() {
        // A hand-edited save file could contain a nonexistent date.
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseStoredValue("2019-02-29T18:00"));
    }

    @Test
    public void parseStoredValue_emptyString_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseStoredValue(""));
    }

    @Test
    public void parseStoredValue_corruptText_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                ScheduleDateTime.parseStoredValue("not-a-date"));
    }

    @Test
    public void parseStoredValue_nullValue_exceptionThrown() {
        assertThrows(NullPointerException.class, () ->
                ScheduleDateTime.parseStoredValue(null));
    }

    // =====================================================================
    // format(LocalDateTime)
    // =====================================================================
    //
    // Midnight is displayed as a bare date; any other time gets a 12-hour
    // clock appended. The tests below fix both branches and the boundary
    // between them.

    @Test
    public void formatDateTime_midnight_dateOnlyReturned() {
        // The whole point of the midnight branch: a task entered as a plain
        // date must not display a misleading "12:00 AM".
        assertEquals("Dec 2 2019",
                ScheduleDateTime.format(LocalDateTime.of(2019, 12, 2, 0, 0)));
    }

    @Test
    public void formatDateTime_oneMinuteAfterMidnight_timeIncluded() {
        // The first instant that is *not* midnight, so it takes the other
        // branch. This also checks that 00:01 renders as 12:01 AM, not 0:01.
        assertEquals("Dec 2 2019, 12:01 AM",
                ScheduleDateTime.format(LocalDateTime.of(2019, 12, 2, 0, 1)));
    }

    @Test
    public void formatDateTime_morningTime_amTimeIncluded() {
        assertEquals("Jan 5 2019, 9:30 AM",
                ScheduleDateTime.format(LocalDateTime.of(2019, 1, 5, 9, 30)));
    }

    @Test
    public void formatDateTime_afternoonTime_pmTimeIncluded() {
        assertEquals("Dec 2 2019, 6:00 PM",
                ScheduleDateTime.format(LocalDateTime.of(2019, 12, 2, 18, 0)));
    }

    @Test
    public void formatDateTime_noon_pmTimeIncluded() {
        // Noon is the classic 12-hour clock trap: it must be 12:00 PM, and it
        // must not be mistaken for midnight by the date-only branch.
        assertEquals("Dec 2 2019, 12:00 PM",
                ScheduleDateTime.format(LocalDateTime.of(2019, 12, 2, 12, 0)));
    }

    @Test
    public void formatDateTime_lastMinuteOfDay_timeIncluded() {
        assertEquals("Dec 2 2019, 11:59 PM",
                ScheduleDateTime.format(LocalDateTime.of(2019, 12, 2, 23, 59)));
    }

    @Test
    public void formatDateTime_secondsPresent_secondsOmitted() {
        // The display pattern has no seconds field, so a non-zero second is
        // dropped rather than rounding the displayed minute.
        assertEquals("Dec 2 2019, 6:00 PM",
                ScheduleDateTime.format(LocalDateTime.of(2019, 12, 2, 18, 0, 45)));
    }

    @Test
    public void formatDateTime_singleDigitDay_dayNotPadded() {
        // The "d" pattern letter means the day is not zero-padded.
        assertEquals("Mar 7 2020, 8:05 AM",
                ScheduleDateTime.format(LocalDateTime.of(2020, 3, 7, 8, 5)));
    }

    @Test
    public void formatDateTime_nullValue_exceptionThrown() {
        assertThrows(NullPointerException.class, () ->
                ScheduleDateTime.format((LocalDateTime) null));
    }

    // =====================================================================
    // format(LocalDate)
    // =====================================================================
    //
    // The overload used when there is no time to show at all.

    @Test
    public void formatDate_typicalDate_friendlyDateReturned() {
        assertEquals("Oct 15 2019",
                ScheduleDateTime.format(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void formatDate_singleDigitDay_dayNotPadded() {
        assertEquals("Jan 5 2019",
                ScheduleDateTime.format(LocalDate.of(2019, 1, 5)));
    }

    @Test
    public void formatDate_leapDay_friendlyDateReturned() {
        assertEquals("Feb 29 2020",
                ScheduleDateTime.format(LocalDate.of(2020, 2, 29)));
    }

    @Test
    public void formatDate_nullValue_exceptionThrown() {
        assertThrows(NullPointerException.class, () ->
                ScheduleDateTime.format((LocalDate) null));
    }

    // =====================================================================
    // Round trips across methods
    // =====================================================================
    //
    // The methods are used together in the real app, so these tests guard the
    // seams between them rather than any single method.

    @Test
    public void parseUserInputThenFormat_dateOnlyEntry_dateOnlyDisplayed() {
        // A user typing a bare date should see a bare date echoed back.
        assertEquals("Dec 2 2019",
                ScheduleDateTime.format(ScheduleDateTime.parseUserInput("2019-12-02")));
    }

    @Test
    public void parseUserInputThenFormat_dateTimeEntry_timeDisplayed() {
        assertEquals("Dec 2 2019, 6:00 PM",
                ScheduleDateTime.format(ScheduleDateTime.parseUserInput("2/12/2019 1800")));
    }

    @Test
    public void parseStoredValue_afterSaving_originalValueRecovered() {
        // Storage saves via LocalDateTime.toString(), so parsing that string
        // must return exactly what was saved. This is the save/load contract.
        LocalDateTime original = LocalDateTime.of(2019, 12, 2, 18, 0);
        assertEquals(original, ScheduleDateTime.parseStoredValue(original.toString()));
    }

    @Test
    public void parseStoredValue_afterSavingMidnight_originalValueRecovered() {
        LocalDateTime original = LocalDateTime.of(2019, 12, 2, 0, 0);
        assertEquals(original, ScheduleDateTime.parseStoredValue(original.toString()));
    }
}
