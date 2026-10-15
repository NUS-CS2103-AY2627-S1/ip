package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import happybot.task.Deadline;

public class ParserDeadlineTest {
    private static final String DEADLINE_USAGE = "Use: deadline <description> /by <yyyy-MM-dd>.";

    private static final String DATE_FORMAT_HINT =
            "Please write dates as yyyy-MM-dd or d/M/yyyy, with an optional 24-hour time that "
                    + "defaults to 0000, such as 2019-12-02 1800.";

    private static final String NO_SUCH_DATE_HINT =
            "There is no such date on the calendar. Please enter a valid date, checking the "
                    + "number of days the month has.";

    private static final String PIPE_MESSAGE = "A task cannot contain the '|' character.";

    @Test
    public void parseDeadline_isoDateWithoutTime_deadlineStartsAtMidnight() throws HappyBotException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-12-02");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getDueDateTime());
    }

    @Test
    public void parseDeadline_slashDateWithoutTime_deadlineStartsAtMidnight() throws HappyBotException {
        // The d/M/yyyy pattern accepts a one-digit day and month, so 2/12/2019 is the same date.
        Deadline deadline = Parser.parseDeadline("return book /by 2/12/2019");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getDueDateTime());
    }

    @Test
    public void parseDeadline_dateWithTime_timeKept() throws HappyBotException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-12-02 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getDueDateTime());
    }

    @Test
    public void parseDeadlineDetails_dateOnlyAndExplicitMidnight_timePresenceKept()
            throws HappyBotException {
        Parser.DeadlineDetails dateOnly = Parser.parseDeadlineDetails("return book /by 2019-12-02");
        Parser.DeadlineDetails explicitMidnight =
                Parser.parseDeadlineDetails("return book /by 2019-12-02 0000");

        assertFalse(dateOnly.getDueDateTime().hasTime());
        assertTrue(explicitMidnight.getDueDateTime().hasTime());
        assertEquals(dateOnly.getDueDateTime().getDateTime(),
                explicitMidnight.getDueDateTime().getDateTime());
    }

    @Test
    public void parseDeadline_extraSpacesAroundDate_deadlineParsed() throws HappyBotException {
        // Parser collapses runs of spaces before splitting the date from the time.
        Deadline deadline = Parser.parseDeadline("return book /by    2019-12-02    1800   ");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getDueDateTime());
    }

    @Test
    public void parseDeadline_descriptionContainsBy_onlyMarkerSplitsCommand() throws HappyBotException {
        // "by" only separates the two parts when it is written as the " /by " marker.
        Deadline deadline = Parser.parseDeadline("stand by the door /by 2019-12-02");

        assertEquals("stand by the door", deadline.getDescription());
    }

    @Test
    public void parseDeadline_spacesAroundDescription_descriptionTrimmed() throws HappyBotException {
        // Stray spaces must not reach the stored description, where they would be saved to the
        // data file and shown in every listing.
        Deadline deadline = Parser.parseDeadline("   return book  /by 2019-12-02");

        assertEquals("return book", deadline.getDescription());
    }

    @Test
    public void parseDeadline_dayPastEndOfMonth_exceptionThrown() {
        // November has 30 days, so this date names no real day and must be refused rather than
        // quietly moved to 30 November.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-11-31"));

        assertEquals(NO_SUCH_DATE_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_dayPastEndOfFebruary_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-02-30"));

        assertEquals(NO_SUCH_DATE_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_leapDayInNonLeapYear_exceptionThrown() {
        // 2019 is not a leap year, so it has no 29 February.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-02-29"));

        assertEquals(NO_SUCH_DATE_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_leapDayInLeapYear_deadlineCreated() throws HappyBotException {
        // 2020 is a leap year, so 29 February exists and must still be accepted.
        Deadline deadline = Parser.parseDeadline("return book /by 2020-02-29");

        assertEquals(LocalDateTime.of(2020, 2, 29, 0, 0), deadline.getDueDateTime());
    }

    @Test
    public void parseDeadline_dayPastEndOfMonthInSlashDate_exceptionThrown() {
        // The same rule applies to the other date pattern.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 31/11/2019"));

        assertEquals(NO_SUCH_DATE_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_dayOutsideAnyMonth_formatHintGiven() {
        // No month has 32 days, so this is a badly written date rather than a missing one.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-11-32"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_monthOutsideYear_formatHintGiven() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-13-05"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_newDeadline_notDone() throws HappyBotException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-12-02");

        assertFalse(deadline.isDone());
    }

    @Test
    public void parseDeadline_noMarker_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book 2019-12-02"));

        assertEquals(DEADLINE_USAGE, e.getMessage());
    }

    @Test
    public void parseDeadline_noDueDate_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("do homework /by       "));

        assertEquals(DEADLINE_USAGE, e.getMessage());
    }

    @Test
    public void parseDeadline_noDescription_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline(" /by 2019-12-02"));

        assertEquals(DEADLINE_USAGE, e.getMessage());
    }

    @Test
    public void parseDeadline_emptyBody_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseDeadline(""));

        assertEquals(DEADLINE_USAGE, e.getMessage());
    }

    @Test
    public void parseDeadline_unreadableDate_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by tomorrow"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_unreadableTime_exceptionThrown() {
        // 6pm has to be written as the 24-hour time 1800.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-12-02 6pm"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_readableDateWithUnreadableTime_formatHintGiven() {
        // The date is fine here, so the message has to be about the time rather than the calendar.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-12-02 2500"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_pipeInDescription_exceptionThrown() {
        // The data file separates fields with a vertical bar, so a task may not hold one.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book | CD /by 2019-12-02"));

        assertEquals(PIPE_MESSAGE, e.getMessage());
    }

    @Test
    public void parseDeadline_duplicateByMarker_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-12-02 /by 2019-12-03"));

        assertEquals(DEADLINE_USAGE, e.getMessage());
    }
}
