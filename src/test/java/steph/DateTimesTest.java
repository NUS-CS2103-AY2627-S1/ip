package steph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DateTimes}.
 *
 * <p>These four methods are the single place the whole program agrees on how a
 * date-time is read from user input, read from the save file, written to the
 * save file, and shown to the user. Each one branches (time present vs absent,
 * ISO date-time vs plain date, midnight vs not), takes only its argument, and
 * is deterministic -- so they are both worth locking down and easy to test.
 *
 * <p>{@code parseUserInput} and {@code parseStored} are package-private; this
 * test lives in package {@code steph} so it can call them directly.
 */
public class DateTimesTest {

    // ====================================================================
    // parseUserInput -- "yyyy-mm-dd" or "yyyy-mm-dd HHmm"
    // ====================================================================

    @Test
    public void parseUserInput_dateOnly_timeIsStartOfDay() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0),
                DateTimes.parseUserInput("2019-10-15"));
    }

    @Test
    public void parseUserInput_dateAndTime_timeParsed() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0),
                DateTimes.parseUserInput("2019-10-15 1800"));
    }

    @Test
    public void parseUserInput_dateAndMidnightTime_parsed() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0),
                DateTimes.parseUserInput("2019-10-15 0000"));
    }

    @Test
    public void parseUserInput_nonIsoDateOrder_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateTimes.parseUserInput("15/10/2019"));
    }

    @Test
    public void parseUserInput_unpaddedMonthAndDay_exceptionThrown() {
        // ISO_LOCAL_DATE requires zero-padded fields.
        assertThrows(DateTimeParseException.class, () -> DateTimes.parseUserInput("2019-1-5"));
    }

    @Test
    public void parseUserInput_word_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateTimes.parseUserInput("tomorrow"));
    }

    @Test
    public void parseUserInput_empty_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateTimes.parseUserInput(""));
    }

    @Test
    public void parseUserInput_impossibleMonth_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateTimes.parseUserInput("2019-13-01"));
    }

    @Test
    public void parseUserInput_impossibleMinute_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateTimes.parseUserInput("2019-10-15 1860"));
    }

    // ====================================================================
    // parseStored -- ISO LocalDateTime ("...T18:00") or plain ISO date
    // ====================================================================

    @Test
    public void parseStored_isoDateTime_parsed() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0),
                DateTimes.parseStored("2019-10-15T18:00"));
    }

    @Test
    public void parseStored_plainDate_timeIsStartOfDay() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0),
                DateTimes.parseStored("2019-10-15"));
    }

    @Test
    public void parseStored_garbage_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateTimes.parseStored("not-a-date"));
    }

    @Test
    public void parseStored_empty_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateTimes.parseStored(""));
    }

    // ====================================================================
    // toStorageFormat / parseStored round-trip
    //
    // The save file stays readable only if whatever toStorageFormat writes,
    // parseStored can read back to the same value.
    // ====================================================================

    @Test
    public void toStorageFormat_thenParseStored_roundTripsValueWithTime() {
        LocalDateTime withTime = LocalDateTime.of(2019, 10, 15, 18, 30);
        assertEquals(withTime, DateTimes.parseStored(DateTimes.toStorageFormat(withTime)));
    }

    @Test
    public void toStorageFormat_thenParseStored_roundTripsMidnightValue() {
        LocalDateTime midnight = LocalDateTime.of(2019, 10, 15, 0, 0);
        assertEquals(midnight, DateTimes.parseStored(DateTimes.toStorageFormat(midnight)));
    }

    @Test
    public void toStorageFormat_valueWithTime_isIso8601() {
        assertEquals("2019-10-15T18:00",
                DateTimes.toStorageFormat(LocalDateTime.of(2019, 10, 15, 18, 0)));
    }

    // ====================================================================
    // toDisplayFormat -- drop the time only when it is exactly midnight
    // ====================================================================

    @Test
    public void toDisplayFormat_midnight_showsDateOnly() {
        assertEquals("Oct 15 2019",
                DateTimes.toDisplayFormat(LocalDateTime.of(2019, 10, 15, 0, 0)));
    }

    @Test
    public void toDisplayFormat_afternoon_showsDateAndTime() {
        // AM/PM marker case varies by JDK locale data, so compare lower-cased.
        assertEquals("oct 15 2019, 6:00pm",
                DateTimes.toDisplayFormat(LocalDateTime.of(2019, 10, 15, 18, 0)).toLowerCase());
    }

    @Test
    public void toDisplayFormat_noon_showsTwelvePm() {
        assertEquals("oct 15 2019, 12:00pm",
                DateTimes.toDisplayFormat(LocalDateTime.of(2019, 10, 15, 12, 0)).toLowerCase());
    }

    @Test
    public void toDisplayFormat_oneMinutePastMidnight_stillShowsTime() {
        // Boundary: only 00:00 is treated as "date only"; 00:01 keeps its time.
        assertEquals("oct 15 2019, 12:01am",
                DateTimes.toDisplayFormat(LocalDateTime.of(2019, 10, 15, 0, 1)).toLowerCase());
    }

    // ====================================================================
    // expandIfMidnight -- a midnight boundary means "through the end of
    // that day," so it expands to the start of the next day; any other
    // time is left unchanged.
    // ====================================================================

    @Test
    public void expandIfMidnight_midnight_returnsStartOfNextDay() {
        assertEquals(LocalDateTime.of(2019, 10, 16, 0, 0),
                DateTimes.expandIfMidnight(LocalDateTime.of(2019, 10, 15, 0, 0)));
    }

    @Test
    public void expandIfMidnight_oneMinutePastMidnight_returnedUnchanged() {
        LocalDateTime oneMinutePastMidnight = LocalDateTime.of(2019, 10, 15, 0, 1);
        assertEquals(oneMinutePastMidnight, DateTimes.expandIfMidnight(oneMinutePastMidnight));
    }

    @Test
    public void expandIfMidnight_afternoon_returnedUnchanged() {
        LocalDateTime afternoon = LocalDateTime.of(2019, 10, 15, 14, 0);
        assertEquals(afternoon, DateTimes.expandIfMidnight(afternoon));
    }
}
