package goat.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import goat.GoatException;

/**
 * Tests the conversions between the text a user types and the moments Goat
 * stores.
 *
 * These are worth testing closely because they sit at the two edges of the
 * program where text becomes data: everything a user types and everything in
 * the save file passes through here, and a mistake would either reject input
 * that should be accepted or silently store the wrong moment.
 */
public class DateTimesTest {

    @Test
    public void parse_dateOnly_storedAtMidnight() throws GoatException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), DateTimes.parse("2019-12-02"));
    }

    @Test
    public void parse_dateAndTime_storedAtThatTime() throws GoatException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimes.parse("2019-12-02 1800"));
    }

    @Test
    public void parse_midnightWrittenAsTime_sameAsDateOnly() throws GoatException {
        // Goat treats midnight as "no time was given", so these must agree.
        assertEquals(DateTimes.parse("2019-12-02"), DateTimes.parse("2019-12-02 0000"));
    }

    @Test
    public void parse_surroundingSpaces_trimmed() throws GoatException {
        // Parser hands over the raw text after "/by", which keeps its spaces.
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimes.parse("   2019-12-02 1800 "));
    }

    @Test
    public void parse_lastMinuteOfDay_parsed() throws GoatException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 23, 59), DateTimes.parse("2019-12-02 2359"));
    }

    @Test
    public void parse_leapDayInLeapYear_parsed() throws GoatException {
        assertEquals(LocalDateTime.of(2020, 2, 29, 0, 0), DateTimes.parse("2020-02-29"));
    }

    @Test
    public void parse_leapDayInCommonYear_exceptionThrown() {
        // 2019 is not a leap year, so this names a day that does not exist.
        assertThrows(GoatException.class, () -> DateTimes.parse("2019-02-29"));
    }

    @Test
    public void parse_impossibleDay_exceptionThrown() {
        assertThrows(GoatException.class, () -> DateTimes.parse("2019-02-30"));
    }

    @Test
    public void parse_monthOutOfRange_exceptionThrown() {
        assertThrows(GoatException.class, () -> DateTimes.parse("2019-13-01"));
    }

    @Test
    public void parse_hourOutOfRange_exceptionThrown() {
        assertThrows(GoatException.class, () -> DateTimes.parse("2019-12-02 2500"));
    }

    @Test
    public void parse_wordyDate_exceptionThrown() {
        assertThrows(GoatException.class, () -> DateTimes.parse("next Friday"));
    }

    @Test
    public void parse_differentDateFormat_exceptionThrown() {
        // The format someone is most likely to reach for by hand.
        assertThrows(GoatException.class, () -> DateTimes.parse("2 Dec 2019"));
    }

    @Test
    public void parse_slashSeparators_exceptionThrown() {
        assertThrows(GoatException.class, () -> DateTimes.parse("2019/12/02"));
    }

    @Test
    public void parse_timeWithColon_exceptionThrown() {
        // "1800" is accepted; "18:00" is not.
        assertThrows(GoatException.class, () -> DateTimes.parse("2019-12-02 18:00"));
    }

    @Test
    public void parse_empty_exceptionThrown() {
        assertThrows(GoatException.class, () -> DateTimes.parse(""));
    }

    @Test
    public void parse_unreadableText_messageShowsTheTextAndBothFormats() {
        GoatException e = assertThrows(GoatException.class, () -> DateTimes.parse(" 2 Dec 2019 "));
        // The message quotes the trimmed text, not what the user typed verbatim.
        assertTrue(e.getMessage().contains("'2 Dec 2019'"), e.getMessage());
        assertTrue(e.getMessage().contains("yyyy-MM-dd"), e.getMessage());
        assertTrue(e.getMessage().contains("yyyy-MM-dd HHmm"), e.getMessage());
    }

    @Test
    public void format_midnight_showsDateWithoutTime() {
        assertEquals("Dec 02 2019", DateTimes.format(LocalDateTime.of(2019, 12, 2, 0, 0)));
    }

    @Test
    public void format_afternoon_showsLowercasePm() {
        assertEquals("Dec 02 2019, 6:00pm", DateTimes.format(LocalDateTime.of(2019, 12, 2, 18, 0)));
    }

    @Test
    public void format_morning_showsLowercaseAm() {
        assertEquals("Aug 06 2019, 9:05am", DateTimes.format(LocalDateTime.of(2019, 8, 6, 9, 5)));
    }

    @Test
    public void format_noon_showsTwelvePm() {
        assertEquals("Aug 06 2019, 12:30pm", DateTimes.format(LocalDateTime.of(2019, 8, 6, 12, 30)));
    }

    @Test
    public void format_oneMinutePastMidnight_showsTwelveAm() {
        // Just past the moment that means "no time given", so a time must show.
        assertEquals("Aug 06 2019, 12:01am", DateTimes.format(LocalDateTime.of(2019, 8, 6, 0, 1)));
    }

    @Test
    public void toFileFormat_midnight_writesTheTimeAnyway() {
        // Unlike format(), the file always carries a time so one pattern reads
        // every saved line.
        assertEquals("2019-12-02 0000", DateTimes.toFileFormat(LocalDateTime.of(2019, 12, 2, 0, 0)));
    }

    @Test
    public void toFileFormat_withTime_writesTheTime() {
        assertEquals("2019-12-02 1800", DateTimes.toFileFormat(LocalDateTime.of(2019, 12, 2, 18, 0)));
    }

    @Test
    public void toFileFormat_thenParse_roundTripsUnchanged() throws GoatException {
        LocalDateTime original = LocalDateTime.of(2019, 8, 6, 14, 30);
        assertEquals(original, DateTimes.parse(DateTimes.toFileFormat(original)));
    }

    @Test
    public void toFileFormat_thenParse_roundTripsMidnightUnchanged() throws GoatException {
        LocalDateTime midnight = LocalDateTime.of(2019, 8, 6, 0, 0);
        assertEquals(midnight, DateTimes.parse(DateTimes.toFileFormat(midnight)));
    }
}
