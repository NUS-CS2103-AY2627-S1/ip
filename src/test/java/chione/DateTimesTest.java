package chione;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link DateTimes}, which is where every date format Chione uses lives.
 *
 * <p>The three formats are checked separately, and then together: what is written
 * to the save file has to read back as the same moment, or a saved task would
 * quietly change date every time it was reloaded.
 */
public class DateTimesTest {
    @Test
    public void parse_dateOnly_takenAsMidnight() throws ChioneException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), DateTimes.parse("2019-10-15"));
    }

    @Test
    public void parse_dateAndTime_bothKept() throws ChioneException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), DateTimes.parse("2019-10-15 1800"));
    }

    @Test
    public void parse_singleDigitMonthAndDay_stillNeedsTwoDigits() {
        // The format is fixed at yyyy-MM-dd, so "2019-1-5" is not a shorter way of
        // writing the same day: it is a date Chione cannot read.
        assertThrows(ChioneException.class, () -> DateTimes.parse("2019-1-5"));
    }

    @Test
    public void parse_dayThatDoesNotExist_exceptionThrown() {
        // A date can be the right shape and still be no date at all.
        assertThrows(ChioneException.class, () -> DateTimes.parse("2019-02-30"));
    }

    @Test
    public void parse_dayThatDoesNotExistWithTime_exceptionThrown() {
        // With a time attached the date goes through a different formatter,
        // which left to itself would move Feb 30 to Feb 28 instead of refusing.
        assertThrows(ChioneException.class, () -> DateTimes.parse("2019-02-30 1800"));
    }

    @Test
    public void parse_leapDayInLeapYear_accepted() throws ChioneException {
        assertEquals(LocalDateTime.of(2020, 2, 29, 12, 0), DateTimes.parse("2020-02-29 1200"));
    }

    @Test
    public void parse_leapDayInCommonYear_exceptionThrown() {
        assertThrows(ChioneException.class, () -> DateTimes.parse("2019-02-29 1200"));
        assertThrows(ChioneException.class, () -> DateTimes.parse("2019-02-29"));
    }

    @Test
    public void parse_thirteenthMonth_exceptionThrown() {
        assertThrows(ChioneException.class, () -> DateTimes.parse("2019-13-01 1200"));
    }

    @Test
    public void parse_hourPastTwentyThree_exceptionThrown() {
        // "2400" is a common way of writing midnight, but it is not a time of
        // day; the next day's "0000" is.
        assertThrows(ChioneException.class, () -> DateTimes.parse("2019-10-15 2400"));
    }

    @Test
    public void parse_wordInsteadOfDate_exceptionThrown() {
        ChioneException e = assertThrows(ChioneException.class, () -> DateTimes.parse("tomorrow"));
        assertEquals("I can't read the date \"tomorrow\". "
                + "Write it as 2019-10-15, or 2019-10-15 1800 to include a time.", e.getMessage());
    }

    @Test
    public void parse_dayFirstOrder_exceptionThrown() {
        assertThrows(ChioneException.class, () -> DateTimes.parse("15/10/2019"));
    }

    @Test
    public void parseDate_validDate_returnsThatDay() throws ChioneException {
        assertEquals(LocalDate.of(2019, 10, 15), DateTimes.parseDate("2019-10-15"));
    }

    @Test
    public void parseDate_dateWithTime_exceptionThrown() {
        // A day is being asked for, so a time attached to it is not understood.
        assertThrows(ChioneException.class, () -> DateTimes.parseDate("2019-10-15 1800"));
    }

    @Test
    public void parseDate_unreadableText_exceptionThrown() {
        ChioneException e = assertThrows(ChioneException.class, () ->
                DateTimes.parseDate("15/10/2019"));
        assertEquals("I can't read the date \"15/10/2019\". Write it as 2019-10-15.",
                e.getMessage());
    }

    @Test
    public void format_midnight_timeNotShown() {
        assertEquals("Oct 15 2019", DateTimes.format(LocalDateTime.of(2019, 10, 15, 0, 0)));
    }

    @Test
    public void format_afternoon_timeShownInLowerCase() {
        assertEquals("Oct 15 2019, 6:00pm", DateTimes.format(LocalDateTime.of(2019, 10, 15, 18, 0)));
    }

    @Test
    public void format_oneMinutePastMidnight_timeShown() {
        // Only exactly midnight counts as "no time was given", so a minute later
        // has to be visible or the two would be indistinguishable on screen.
        assertEquals("Oct 15 2019, 12:01am",
                DateTimes.format(LocalDateTime.of(2019, 10, 15, 0, 1)));
    }

    @Test
    public void format_noon_shownAsPm() {
        assertEquals("Oct 15 2019, 12:00pm", DateTimes.format(LocalDateTime.of(2019, 10, 15, 12, 0)));
    }

    @Test
    public void formatDate_singleDigitDay_paddedToTwo() {
        assertEquals("Jan 05 2019", DateTimes.formatDate(LocalDate.of(2019, 1, 5)));
    }

    @Test
    public void toSaveFormat_moment_usesTheInputFormat() {
        assertEquals("2019-10-15 1800", DateTimes.toSaveFormat(LocalDateTime.of(2019, 10, 15, 18, 0)));
    }

    @Test
    public void toSaveFormat_midnight_keepsTheZeroedTime() {
        // Midnight has to be written out in full, or the saved line would be
        // shorter than every other line and would not read back the same way.
        assertEquals("2019-10-15 0000", DateTimes.toSaveFormat(LocalDateTime.of(2019, 10, 15, 0, 0)));
    }

    @Test
    public void toSaveFormat_thenParse_givesBackTheSameMoment() throws ChioneException {
        LocalDateTime original = LocalDateTime.of(2019, 10, 15, 18, 30);
        assertEquals(original, DateTimes.parse(DateTimes.toSaveFormat(original)));
    }

    @Test
    public void toSaveFormat_thenParse_survivesMidnight() throws ChioneException {
        LocalDateTime midnight = LocalDateTime.of(2019, 10, 15, 0, 0);
        assertEquals(midnight, DateTimes.parse(DateTimes.toSaveFormat(midnight)));
    }
}
