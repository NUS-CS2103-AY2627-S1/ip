package chud.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TaskDateTimeTest {
    @Test
    void parse_isoDateOnly_parsesAndDisplaysWithoutTime() {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15");

        assertEquals(LocalDate.of(2019, 10, 15), dateTime.getDate());
        assertEquals("Oct 15 2019", dateTime.toString());
    }

    @Test
    void parse_isoDateWithTime_parsesAndDisplaysWithTime() {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15 1800");

        assertEquals(LocalDate.of(2019, 10, 15), dateTime.getDate());
        assertEquals("Oct 15 2019, 6:00PM", dateTime.toString());
    }

    @Test
    void parse_slashDateSingleDigits_parsesDayBeforeMonth() {
        // "2/12/2019" is day 2, month 12 (d/M/yyyy), not month 2, day 12.
        TaskDateTime dateTime = TaskDateTime.parse("2/12/2019");

        assertEquals(LocalDate.of(2019, 12, 2), dateTime.getDate());
        assertEquals("Dec 02 2019", dateTime.toString());
    }

    @Test
    void parse_slashDateWithTime_parsesAndDisplaysWithTime() {
        TaskDateTime dateTime = TaskDateTime.parse("2/12/2019 1800");

        assertEquals(LocalDate.of(2019, 12, 2), dateTime.getDate());
        assertEquals("Dec 02 2019, 6:00PM", dateTime.toString());
    }

    @Test
    void parse_slashDateDoubleDigitDay_parsesDayBeforeMonth() {
        // "12/2/2019" is day 12, month 2 -- confirms day-then-month ordering isn't
        // accidentally swapped once both fields are two digits.
        TaskDateTime dateTime = TaskDateTime.parse("12/2/2019");

        assertEquals(LocalDate.of(2019, 2, 12), dateTime.getDate());
    }

    @Test
    void parse_surroundingWhitespace_isTrimmedBeforeParsing() {
        TaskDateTime dateTime = TaskDateTime.parse("  2019-10-15  ");

        assertEquals(LocalDate.of(2019, 10, 15), dateTime.getDate());
    }

    @Test
    void parse_midnightTime_displaysAsTwelveAm() {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15 0000");

        assertEquals("Oct 15 2019, 12:00AM", dateTime.toString());
    }

    @Test
    void parse_noonTime_displaysAsTwelvePm() {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15 1200");

        assertEquals("Oct 15 2019, 12:00PM", dateTime.toString());
    }

    @Test
    void parse_emptyString_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parse(""));
    }

    @Test
    void parse_unrecognizedText_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                TaskDateTime.parse("not-a-date"));

        assertTrue(exception.getMessage().contains("not-a-date"));
    }

    @Test
    void parse_calendarDateDoesNotExist_exceptionThrown() {
        // February never has 30 days -- this must fail as invalid, not silently roll over
        // into March.
        assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parse("2019-02-30"));
    }

    @Test
    void parse_unsupportedDelimiter_exceptionThrown() {
        // Slashes in year-month-day order aren't one of the accepted formats (only
        // yyyy-MM-dd and d/M/yyyy are), so this must be rejected rather than guessed at.
        assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parse("2019/10/15"));
    }

    @Test
    void getDate_dateTimeWithTimeOfDay_returnsDateWithoutTime() {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15 1800");

        assertEquals(LocalDate.of(2019, 10, 15), dateTime.getDate());
    }

    @Test
    void toStorageString_dateOnly_returnsIsoFormat() {
        TaskDateTime dateTime = TaskDateTime.parse("2/12/2019");

        assertEquals("2019-12-02", dateTime.toStorageString());
    }

    @Test
    void toStorageString_dateWithTime_returnsIsoFormatWithTime() {
        TaskDateTime dateTime = TaskDateTime.parse("2/12/2019 1800");

        assertEquals("2019-12-02 1800", dateTime.toStorageString());
    }

    @Test
    void toStorageString_thenParse_roundTripsToEqualDisplay() {
        TaskDateTime original = TaskDateTime.parse("2/12/2019 1800");

        TaskDateTime reloaded = TaskDateTime.parse(original.toStorageString());

        assertEquals(original.toString(), reloaded.toString());
    }

    @Test
    void toStorageString_dateOnlyThenParse_roundTripsToEqualDisplay() {
        TaskDateTime original = TaskDateTime.parse("2/12/2019");

        TaskDateTime reloaded = TaskDateTime.parse(original.toStorageString());

        assertEquals(original.toString(), reloaded.toString());
    }

    @Test
    void isBefore_earlierDate_returnsTrue() {
        TaskDateTime earlier = TaskDateTime.parse("2026-09-21");
        TaskDateTime later = TaskDateTime.parse("2026-09-22");

        assertTrue(earlier.isBefore(later));
        assertFalse(later.isBefore(earlier));
    }

    @Test
    void isBefore_sameDateEarlierTime_returnsTrue() {
        TaskDateTime earlier = TaskDateTime.parse("2026-09-22 0900");
        TaskDateTime later = TaskDateTime.parse("2026-09-22 1800");

        assertTrue(earlier.isBefore(later));
        assertFalse(later.isBefore(earlier));
    }

    @Test
    void isBefore_equalDateTime_returnsFalse() {
        TaskDateTime first = TaskDateTime.parse("2026-09-22 0900");
        TaskDateTime second = TaskDateTime.parse("2026-09-22 0900");

        assertFalse(first.isBefore(second));
    }

    @Test
    void isBefore_dateOnlyTreatedAsMidnight_isBeforeSameDateWithLaterTime() {
        // A date-only value has no time of day; comparing it must treat it as the start of that
        // date (midnight), not as "no time" (which would make comparison meaningless).
        TaskDateTime dateOnly = TaskDateTime.parse("2026-09-22");
        TaskDateTime sameDateWithTime = TaskDateTime.parse("2026-09-22 0001");

        assertTrue(dateOnly.isBefore(sameDateWithTime));
    }
}
