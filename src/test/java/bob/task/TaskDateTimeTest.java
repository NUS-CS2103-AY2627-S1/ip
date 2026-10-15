package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import bob.BobException;

/**
 * Tests {@link TaskDateTime#parse}, the one way text typed by the user becomes a
 * date the chatbot can reason about.
 *
 * <p>It is tested rather than the other methods because it is where the chatbot
 * decides what it will and will not accept: everything downstream — listing by
 * day, sorting, saving — assumes a {@code TaskDateTime} only exists for text that
 * was understood, and this is the method that has to make that true.
 *
 * <p>A parsed date is checked through what it produces:
 * {@link TaskDateTime#toSaveField()} for the form written to the save file, and
 * {@link TaskDateTime#toString()} for the form shown to the user. Those are what the
 * rest of the chatbot relies on, so they are checked rather than {@code equals}.
 * {@code equals} is tested on its own, above all for the one pair of dates it tells
 * apart that {@link TaskDateTime#compareTo} puts level.
 */
public class TaskDateTimeTest {

    @Test
    public void parse_dayOnly_dayStoredWithoutTime() throws BobException {
        TaskDateTime date = TaskDateTime.parse("2026-12-02");

        assertEquals("2026-12-02", date.toSaveField());
        assertEquals("Dec 02 2026", date.toString());
    }

    @Test
    public void parse_dayAndTime_bothStored() throws BobException {
        TaskDateTime date = TaskDateTime.parse("2026-12-02 1800");

        assertEquals("2026-12-02 1800", date.toSaveField());
        assertEquals("Dec 02 2026 18:00", date.toString());
    }

    @Test
    public void parse_surroundingAndRepeatedWhitespace_ignored() throws BobException {
        TaskDateTime date = TaskDateTime.parse("  2026-12-02   1800  ");

        assertEquals("2026-12-02 1800", date.toSaveField());
    }

    @Test
    public void parse_midnight_keptAsATime() throws BobException {
        TaskDateTime withMidnight = TaskDateTime.parse("2026-12-02 0000");
        TaskDateTime withoutTime = TaskDateTime.parse("2026-12-02");

        // A task due "on the 2nd" and one due "at midnight on the 2nd" are
        // different things, so the second must not lose its time.
        assertEquals("2026-12-02 0000", withMidnight.toSaveField());
        assertNotEquals(withoutTime.toString(), withMidnight.toString());
    }

    @Test
    public void hasTime_dayWithOrWithoutATime_trueOnlyWhenOneWasGiven() throws BobException {
        assertFalse(TaskDateTime.parse("2026-12-02").hasTime());
        assertTrue(TaskDateTime.parse("2026-12-02 1800").hasTime());
        // Midnight is a time the user gave, although it sorts with a day given none.
        assertTrue(TaskDateTime.parse("2026-12-02 0000").hasTime());
    }

    @Test
    public void equals_sameDayAndTimeTypedDifferently_equalWithEqualHashCodes() throws BobException {
        TaskDateTime date = TaskDateTime.parse("2026-12-02 1800");
        TaskDateTime sameDate = TaskDateTime.parse("  2026-12-02   1800 ");

        assertEquals(date, sameDate);
        assertEquals(date.hashCode(), sameDate.hashCode());
    }

    @Test
    public void equals_dayWithoutTimeAndSameDayAtMidnight_notEqualThoughLevelInOrder() throws BobException {
        TaskDateTime withoutTime = TaskDateTime.parse("2026-12-02");
        TaskDateTime atMidnight = TaskDateTime.parse("2026-12-02 0000");

        // Ordering puts the two level, but they were written, and are shown, differently.
        assertEquals(0, withoutTime.compareTo(atMidnight));
        assertNotEquals(withoutTime, atMidnight);
    }

    @Test
    public void equals_differentDayTimeOrType_notEqual() throws BobException {
        TaskDateTime date = TaskDateTime.parse("2026-12-02 1800");

        assertNotEquals(date, TaskDateTime.parse("2026-12-03 1800"));
        assertNotEquals(date, TaskDateTime.parse("2026-12-02 1801"));
        assertNotEquals(date, TaskDateTime.parse("2026-12-02"));
        // The text it was read from is not the date itself.
        assertNotEquals(date, "2026-12-02 1800");
    }

    @Test
    public void parse_boundaryTimes_accepted() throws BobException {
        assertEquals("Dec 02 2026 23:59", TaskDateTime.parse("2026-12-02 2359").toString());
        assertEquals("Dec 02 2026 00:01", TaskDateTime.parse("2026-12-02 0001").toString());
    }

    @Test
    public void parse_leapDayInLeapYear_accepted() throws BobException {
        assertEquals("Feb 29 2028", TaskDateTime.parse("2028-02-29").toString());
    }

    @Test
    public void parse_yearZero_exceptionSaysThereIsNoYearZero() {
        assertEquals("0000-06-01 isn't a real day: there is no year 0.",
                messageFromParsing("0000-06-01"));
    }

    @Test
    public void parse_yearOutsideFourDigits_refusedAsNotADate() {
        // LocalDate would read these signed forms, which the chatbot never mentions:
        // +10000-01-01 was a date while 10000-01-01 was not, which cannot be explained.
        assertTrue(messageFromParsing("+10000-01-01").startsWith("I don't understand"));
        assertTrue(messageFromParsing("-0001-06-01").startsWith("I don't understand"));
        assertTrue(messageFromParsing("10000-01-01").startsWith("I don't understand"));
    }

    @Test
    public void parse_firstAndLastYearAccepted() throws BobException {
        assertEquals("Jan 01 0001", TaskDateTime.parse("0001-01-01").toString());
        assertEquals("Dec 31 9999", TaskDateTime.parse("9999-12-31").toString());
    }

    @Test
    public void formatDay_anyYear_writesTheYearItself() {
        // The format counts the year straight through rather than within an era, so a
        // year at or below zero could not be shown as the year after it.
        assertEquals("Jun 01 0000", TaskDateTime.formatDay(LocalDate.of(0, 6, 1)));
        assertEquals("Jun 01 0001", TaskDateTime.formatDay(LocalDate.of(1, 6, 1)));
    }

    @Test
    public void parse_dayPastTheEndOfItsMonth_exceptionSaysHowLongTheMonthIs() {
        // The 30th of February never happens, and neither does the 29th in a
        // year that is not a leap year.
        assertEquals("2026-02-30 isn't a real day: February 2026 has 28 days.",
                messageFromParsing("2026-02-30"));
        assertEquals("2026-02-29 isn't a real day: February 2026 has 28 days.",
                messageFromParsing("2026-02-29"));
        assertEquals("2028-02-30 isn't a real day: February 2028 has 29 days.",
                messageFromParsing("2028-02-30"));
        assertEquals("2026-04-31 isn't a real day: April 2026 has 30 days.",
                messageFromParsing("2026-04-31"));
    }

    @Test
    public void parse_dayZero_exceptionSaysThereIsNoDayZero() {
        // How long December is says nothing about a day 0, which no month has.
        assertEquals("2026-12-00 isn't a real day: there is no day 0.",
                messageFromParsing("2026-12-00"));
    }

    @Test
    public void parse_monthThatDoesNotExist_exceptionSaysThereIsNoSuchMonth() {
        assertEquals("2026-13-01 isn't a real day: there is no month 13.", messageFromParsing("2026-13-01"));
        assertEquals("2026-00-10 isn't a real day: there is no month 0.", messageFromParsing("2026-00-10"));
    }

    @Test
    public void parse_impossibleDayWithATime_exceptionNamesTheDay() {
        assertEquals("2026-02-30 isn't a real day: February 2026 has 28 days.",
                messageFromParsing("2026-02-30 1800"));
    }

    @Test
    public void parse_dayInAnotherFormat_exceptionThrown() {
        // Only yyyy-mm-dd is accepted, so that 2/12/2026 does not have to be
        // guessed at as either the 2nd of December or the 12th of February.
        assertThrows(BobException.class, () -> TaskDateTime.parse("02/12/2026"));
        assertThrows(BobException.class, () -> TaskDateTime.parse("2026-2-2"));
        assertThrows(BobException.class, () -> TaskDateTime.parse("Dec 02 2026"));
        // Out of the accepted form, a day is not looked into, so it is asked for
        // again in that form rather than said not to exist.
        assertTrue(messageFromParsing("2026-2-30").startsWith("I don't understand \"2026-2-30\" as a date."));
    }

    @Test
    public void parse_timeInAnotherFormat_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskDateTime.parse("2026-12-02 18:00"));
        assertThrows(BobException.class, () -> TaskDateTime.parse("2026-12-02 6pm"));
        assertThrows(BobException.class, () -> TaskDateTime.parse("2026-12-02 800"));
    }

    @Test
    public void parse_timeThatDoesNotExist_exceptionSaysHowHighHoursAndMinutesGo() {
        assertEquals("2500 isn't a real time: hours go up to 23 and minutes up to 59.",
                messageFromParsing("2026-12-02 2500"));
        // Below 2359, but the minutes run past 59.
        assertEquals("1260 isn't a real time: hours go up to 23 and minutes up to 59.",
                messageFromParsing("2026-12-02 1260"));
    }

    @Test
    public void parse_midnightWrittenAs2400_exceptionThrown() {
        // Read leniently, 2400 would quietly become 0000 of the same day, a day
        // earlier than the user meant.
        assertEquals("2400 isn't a real time: hours go up to 23 and minutes up to 59.",
                messageFromParsing("2026-12-02 2400"));
    }

    @Test
    public void parse_moreThanTwoParts_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskDateTime.parse("2026-12-02 1800 1900"));
    }

    @Test
    public void parse_emptyOrBlankText_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskDateTime.parse(""));
        assertThrows(BobException.class, () -> TaskDateTime.parse("   "));
    }

    @Test
    public void parse_textThatIsNotADate_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskDateTime.parse("no idea :-p"));
        assertThrows(BobException.class, () -> TaskDateTime.parse("Sunday"));
    }

    @Test
    public void parse_unreadableText_messageQuotesTheText() {
        BobException exception = assertThrows(BobException.class, () -> TaskDateTime.parse("Sunday"));

        // The offending text is quoted back so that a user who mistyped one of
        // several dates on a line can see which one was not understood.
        assertTrue(exception.getMessage().contains("\"Sunday\""));
    }

    @Test
    public void parse_dayOnly_comparesAgainstTheRightDay() throws BobException {
        TaskDateTime date = TaskDateTime.parse("2026-12-02");

        assertTrue(date.isOn(LocalDate.of(2026, 12, 2)));
        assertTrue(date.isBefore(LocalDate.of(2026, 12, 3)));
        assertTrue(date.isAfter(LocalDate.of(2026, 12, 1)));
    }

    @Test
    public void parseDay_dayInTheAcceptedForm_dayReturned() throws BobException {
        assertEquals(LocalDate.of(2026, 12, 2), TaskDateTime.parseDay("2026-12-02"));
        assertEquals(LocalDate.of(2026, 12, 2), TaskDateTime.parseDay("  2026-12-02  "));
    }

    @Test
    public void parseDay_timeAfterTheDay_exceptionThrown() {
        // Refused rather than quietly treated as the whole day, since a user who
        // typed a time is asking something this chatbot does not answer.
        assertThrows(BobException.class, () -> TaskDateTime.parseDay("2026-12-02 1800"));
    }

    @Test
    public void parseDay_textThatIsNotADay_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskDateTime.parseDay("Sunday"));
        assertThrows(BobException.class, () -> TaskDateTime.parseDay("02/12/2026"));
        assertThrows(BobException.class, () -> TaskDateTime.parseDay(""));
    }

    @Test
    public void parseDay_dayThatDoesNotExist_exceptionSaysWhy() {
        BobException leapException = assertThrows(BobException.class, () ->
                TaskDateTime.parseDay("2026-02-29"));
        BobException monthException = assertThrows(BobException.class, () ->
                TaskDateTime.parseDay("  2026-13-01  "));

        assertEquals("2026-02-29 isn't a real day: February 2026 has 28 days.", leapException.getMessage());
        assertEquals("2026-13-01 isn't a real day: there is no month 13.", monthException.getMessage());
    }

    @Test
    public void formatDay_anyDay_writtenTheWayDatesAreShownBack() {
        assertEquals("Dec 02 2026", TaskDateTime.formatDay(LocalDate.of(2026, 12, 2)));
        // Single-figure days are padded, so a column of dates lines up.
        assertEquals("Jan 05 2026", TaskDateTime.formatDay(LocalDate.of(2026, 1, 5)));
    }

    @Test
    public void compareTo_differentDays_earlierDayFirst() throws BobException {
        TaskDateTime earlier = TaskDateTime.parse("2026-12-02");
        TaskDateTime later = TaskDateTime.parse("2026-12-03");

        assertTrue(earlier.compareTo(later) < 0);
        assertTrue(later.compareTo(earlier) > 0);
    }

    @Test
    public void compareTo_sameDayDifferentTimes_earlierTimeFirst() throws BobException {
        TaskDateTime morning = TaskDateTime.parse("2026-12-02 0900");
        TaskDateTime evening = TaskDateTime.parse("2026-12-02 1800");

        assertTrue(morning.compareTo(evening) < 0);
        assertTrue(evening.compareTo(morning) > 0);
    }

    @Test
    public void compareTo_sameDayOneWithoutATime_theOneWithoutATimeFirst() throws BobException {
        TaskDateTime wholeDay = TaskDateTime.parse("2026-12-02");
        TaskDateTime atNine = TaskDateTime.parse("2026-12-02 0900");

        // A date given without a time counts as the start of its day. That is an
        // ordering rule, not a claim that the task is due at midnight.
        assertTrue(wholeDay.compareTo(atNine) < 0);
    }

    @Test
    public void compareTo_samePointInTime_zero() throws BobException {
        assertEquals(0, TaskDateTime.parse("2026-12-02").compareTo(TaskDateTime.parse("2026-12-02")));
        assertEquals(0, TaskDateTime.parse("2026-12-02 1800")
                .compareTo(TaskDateTime.parse("2026-12-02 1800")));
        // A day with no time and the same day at midnight sort together.
        assertEquals(0, TaskDateTime.parse("2026-12-02").compareTo(TaskDateTime.parse("2026-12-02 0000")));
    }

    @Test
    public void isOnBeforeAfter_dateWithATime_answeredByItsDayAlone() throws BobException {
        TaskDateTime date = TaskDateTime.parse("2026-12-02 1800");

        // The time of day never decides which day a task belongs to.
        assertTrue(date.isOn(LocalDate.of(2026, 12, 2)));
        assertFalse(date.isBefore(LocalDate.of(2026, 12, 2)));
        assertFalse(date.isAfter(LocalDate.of(2026, 12, 2)));
        assertTrue(date.isBefore(LocalDate.of(2026, 12, 3)));
        assertTrue(date.isAfter(LocalDate.of(2026, 12, 1)));
    }

    /** Returns the message of the error that parsing {@code text} throws. */
    private static String messageFromParsing(String text) {
        return assertThrows(BobException.class, () -> TaskDateTime.parse(text)).getMessage();
    }
}
