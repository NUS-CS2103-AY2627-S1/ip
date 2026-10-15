package thomas.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import thomas.ThomasException;

/**
 * Tests {@link Task} itself: the date reading and formats every task type
 * shares, and the completion state they all carry.
 * <p>
 * {@link Task#parseDate} is the piece worth testing hardest. It is static and
 * pure, both the command handling and the save file loader go through it, and it
 * is the one place that decides what counts as a date -- so a mistake in it is
 * felt at the keyboard and in the save file at once. Its failures are already
 * seen through the parser, but only its failures: the parser puts what comes
 * back into a private field, so nothing until now has checked that a date that
 * does parse parses to the right moment.
 * <p>
 * The display formats are pinned for the same reason. They are what the user
 * reads, and a pattern is easy to get subtly wrong -- {@code mm} against
 * {@code MM}, {@code hh} against {@code HH} -- in a way that still compiles and
 * still produces something date-shaped.
 */
public class TaskTest {

    /** Moment the equality tests build their dated tasks around */
    private static final LocalDateTime DEC_02_6PM = LocalDateTime.of(2019, 12, 2, 18, 0);

    // ---- parseDate: dates it accepts ----

    @Test
    public void parseDate_dateAndTime_returnsThatMoment() throws ThomasException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                Task.parseDate("2019-12-02 1800", "a deadline date"));
    }

    @Test
    public void parseDate_afternoonTime_readAsTwentyFourHourClock() throws ThomasException {
        // HH is the 24-hour hour, so 1800 is the evening and not 6am.
        assertEquals(18, Task.parseDate("2019-12-02 1800", "a deadline date").getHour());
    }

    @Test
    public void parseDate_midnight_returnsStartOfDay() throws ThomasException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                Task.parseDate("2019-12-02 0000", "a deadline date"));
    }

    @Test
    public void parseDate_lastMinuteOfDay_returnsThatMoment() throws ThomasException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 23, 59),
                Task.parseDate("2019-12-02 2359", "a deadline date"));
    }

    @Test
    public void parseDate_monthAndMinutesDiffer_bothReadCorrectly() throws ThomasException {
        // mm is minutes and MM is months. Reading a date whose month and minutes differ is what would catch the two
        // being swapped in the pattern; a date such as 2019-12-12 1212 would not.
        LocalDateTime moment = Task.parseDate("2019-03-04 0745", "a deadline date");

        assertEquals(3, moment.getMonthValue());
        assertEquals(45, moment.getMinute());
        assertEquals(4, moment.getDayOfMonth());
        assertEquals(7, moment.getHour());
    }

    // ---- parseDate: dates it refuses ----

    @Test
    public void parseDate_dateWithoutTime_exceptionThrown() {
        // A date alone is refused rather than assumed to mean midnight.
        assertThrows(ThomasException.class, () -> Task.parseDate("2019-12-02", "a deadline date"));
    }

    @Test
    public void parseDate_timeWithoutDate_exceptionThrown() {
        assertThrows(ThomasException.class, () -> Task.parseDate("1800", "a deadline date"));
    }

    @Test
    public void parseDate_unpaddedTime_exceptionThrown() {
        // The hour and minute are four digits, so a three-digit time is not a time.
        assertThrows(ThomasException.class, () -> Task.parseDate("2019-12-02 800", "a deadline date"));
    }

    @Test
    public void parseDate_twelveHourTime_exceptionThrown() {
        assertThrows(ThomasException.class, () -> Task.parseDate("2019-12-02 6pm", "a deadline date"));
    }

    @Test
    public void parseDate_wordsInsteadOfDate_exceptionThrown() {
        assertThrows(ThomasException.class, () -> Task.parseDate("tomorrow", "a deadline date"));
    }

    @Test
    public void parseDate_emptyText_exceptionThrown() {
        assertThrows(ThomasException.class, () -> Task.parseDate("", "a deadline date"));
    }

    @Test
    public void parseDate_impossibleMonth_exceptionThrown() {
        // A well-formed date that does not exist is still refused.
        assertThrows(ThomasException.class, () -> Task.parseDate("2019-13-02 1800", "a deadline date"));
    }

    @Test
    public void parseDate_dayOutsideItsMonth_exceptionThrown() {
        // A day that does not exist in its month is refused. The formatter resolves strictly for exactly this:
        // resolved smartly, the default, "2019-02-30 1800" would be accepted and stored as the 28th with nothing said.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Task.parseDate("2019-02-30 1800", "a deadline date"));
        assertEquals("There's no such moment as '2019-02-30 1800' for a deadline date! "
                + "Check the day is on the calendar and the time is on the 24-hour clock, 0000 to 2359.",
                e.getMessage());
    }

    @Test
    public void parseDate_thirtyFirstOfAThirtyDayMonth_exceptionThrown() {
        assertThrows(ThomasException.class, () -> Task.parseDate("2019-04-31 0900", "a deadline date"));
    }

    @Test
    public void parseDate_leapDayInALeapYear_returnsThatMoment() throws ThomasException {
        // Both sides of the leap-year boundary: the 29th exists in 2020 and not in 2019.
        assertEquals(LocalDateTime.of(2020, 2, 29, 18, 0), Task.parseDate("2020-02-29 1800", "a deadline date"));
    }

    @Test
    public void parseDate_leapDayInACommonYear_exceptionThrown() {
        assertThrows(ThomasException.class, () -> Task.parseDate("2019-02-29 1800", "a deadline date"));
    }

    @Test
    public void parseDate_hourTwentyFour_exceptionThrown() {
        // Strict resolution likewise refuses hour 24, which smart resolution would roll forward to midnight the next
        // day. Midnight is written 0000, which parseDate_midnight_returnsStartOfDay covers.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Task.parseDate("2019-12-02 2400", "a deadline date"));
        assertEquals("There's no such moment as '2019-12-02 2400' for a deadline date! "
                + "Check the day is on the calendar and the time is on the 24-hour clock, 0000 to 2359.",
                e.getMessage());
    }

    @Test
    public void parseDate_impossibleMonth_messageSaysTheMomentDoesNotExist() {
        // The right shape naming a thirteenth month is told the moment does not exist, not what the shape should be.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Task.parseDate("2019-13-02 1800", "a deadline date"));
        assertEquals("There's no such moment as '2019-13-02 1800' for a deadline date! "
                + "Check the day is on the calendar and the time is on the 24-hour clock, 0000 to 2359.",
                e.getMessage());
    }

    @Test
    public void parseDate_wrongShape_messageGivesTheFormat() {
        // Text that is not in the format at all is shown the format. The two messages are told apart by shape, so a
        // near miss of the right width but wrong punctuation gets this one.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Task.parseDate("2019/12/02 1800", "a deadline date"));
        assertEquals("I can't read '2019/12/02 1800' as a deadline date! "
                + "My timetable wants a date and a 24-hour time, like 2019-12-02 1800.", e.getMessage());
    }

    @Test
    public void parseDate_unreadableDate_messageNamesTheFieldAndTheText() {
        // The field name is passed in so each date can name itself, and carries its own article so the message reads
        // properly for all three.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Task.parseDate("Mon 2pm", "a start date"));

        assertEquals("I can't read 'Mon 2pm' as a start date! "
                + "My timetable wants a date and a 24-hour time, like 2019-12-02 1800.", e.getMessage());
    }

    @Test
    public void parseDate_unreadableEndDate_messageNamesThatField() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Task.parseDate("4pm", "an end date"));

        assertEquals("I can't read '4pm' as an end date! "
                + "My timetable wants a date and a 24-hour time, like 2019-12-02 1800.", e.getMessage());
    }

    // ---- the formats ----

    @Test
    public void inputFormat_writingThenReading_returnsTheSameMoment() throws ThomasException {
        // Reading and writing go through the same format, which is what makes whatever is saved something the parser
        // can read back. This holds the two together: if the input format changed, this would fail rather than the
        // save file quietly becoming unreadable.
        LocalDateTime moment = LocalDateTime.of(2019, 12, 2, 18, 0);

        String written = moment.format(Task.DATE_INPUT_FORMAT);

        assertEquals("2019-12-02 1800", written);
        assertEquals(moment, Task.parseDate(written, "a deadline date"));
    }

    @Test
    public void displayFormat_afternoonMoment_showsShortMonthAndTwelveHourTime() {
        // What the user reads: deliberately not the format the date is typed in.
        assertEquals("Dec 02 2019, 6:00 PM",
                LocalDateTime.of(2019, 12, 2, 18, 0).format(Task.DATE_DISPLAY_FORMAT));
    }

    @Test
    public void displayFormat_morningMoment_showsAmMarker() {
        assertEquals("Mar 04 2019, 7:45 AM",
                LocalDateTime.of(2019, 3, 4, 7, 45).format(Task.DATE_DISPLAY_FORMAT));
    }

    @Test
    public void displayDayFormat_wholeDay_showsNoTime() {
        // A whole day has no hour to show, so it has a format of its own.
        assertEquals("Dec 02 2019", LocalDate.of(2019, 12, 2).format(Task.DATE_DISPLAY_DAY));
    }

    // ---- the formats do not follow the machine's language ----

    /**
     * Runs an action with the JVM's default locale set to another language, and
     * puts the original back afterwards whatever happens.
     * This is the automated form of running the chatbot on a machine set to
     * Chinese: DateTimeFormatter.ofPattern follows the default locale unless one
     * is pinned, and the formats pin English so the month names and am/pm
     * markers read the same everywhere.
     *
     * @param locale Language to run under.
     * @param action What to check while that language is the default.
     */
    private static void underDefaultLocale(Locale locale, Runnable action) {
        Locale original = Locale.getDefault();
        Locale.setDefault(locale);
        try {
            action.run();
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void displayFormat_chineseDefaultLocale_stillShowsEnglishMonthAndMarker() {
        underDefaultLocale(Locale.CHINA, () -> assertEquals("Dec 02 2019, 6:00 PM",
                LocalDateTime.of(2019, 12, 2, 18, 0).format(Task.DATE_DISPLAY_FORMAT)));
    }

    @Test
    public void displayDayFormat_chineseDefaultLocale_stillShowsEnglishMonth() {
        underDefaultLocale(Locale.CHINA, () -> assertEquals("Dec 02 2019",
                LocalDate.of(2019, 12, 2).format(Task.DATE_DISPLAY_DAY)));
    }

    @Test
    public void displayFormat_germanDefaultLocale_stillShowsEnglishMonthAndMarker() {
        // German has its own month abbreviations ("Dez") and no AM/PM, so a
        // format following the default would show both differently.
        underDefaultLocale(Locale.GERMANY, () -> assertEquals("Dec 02 2019, 6:00 PM",
                LocalDateTime.of(2019, 12, 2, 18, 0).format(Task.DATE_DISPLAY_FORMAT)));
    }

    @Test
    public void parseDate_chineseDefaultLocale_stillReadsTheDate() {
        // The input format is digits only, but its locale is pinned too: some
        // locales use their own digits, and the parser must not start expecting them.
        underDefaultLocale(Locale.CHINA, () -> {
            try {
                assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                        Task.parseDate("2019-12-02 1800", "a deadline date"));
            } catch (ThomasException e) {
                throw new AssertionError("Date refused under a Chinese default locale", e);
            }
        });
    }

    @Test
    public void toSaveFormat_chineseDefaultLocale_writesTheSameLine() {
        // What is written must be readable on any machine the file is copied to.
        underDefaultLocale(Locale.CHINA, () -> assertEquals("D | 0 | return book | 2019-12-02 1800",
                new DeadlineTask("return book", DEC_02_6PM).toSaveFormat()));
    }

    // ---- completion state ----

    @Test
    public void getStatusIcon_newTask_isNotDone() {
        assertEquals("[ ]", new Task("read book").getStatusIcon());
    }

    @Test
    public void markAsDone_notDoneTask_becomesDone() {
        Task task = new Task("read book");

        task.markAsDone();

        assertEquals("[X]", task.getStatusIcon());
    }

    @Test
    public void markAsDone_alreadyDoneTask_staysDone() {
        // Marking a task that is already done leaves it done rather than toggling.
        Task task = new Task("read book");

        task.markAsDone();
        task.markAsDone();

        assertEquals("[X]", task.getStatusIcon());
    }

    @Test
    public void unmarkAsDone_doneTask_becomesNotDone() {
        Task task = new Task("read book");
        task.markAsDone();

        task.unmarkAsDone();

        assertEquals("[ ]", task.getStatusIcon());
    }

    @Test
    public void unmarkAsDone_notDoneTask_staysNotDone() {
        // Likewise, unmarking a task that was never done is not a toggle.
        Task task = new Task("read book");

        task.unmarkAsDone();

        assertEquals("[ ]", task.getStatusIcon());
    }

    @Test
    public void isDone_newTask_isFalse() {
        assertFalse(new Task("read book").isDone());
    }

    @Test
    public void isDone_markedTask_isTrue() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone());
    }

    @Test
    public void isDone_unmarkedTask_isFalse() {
        Task task = new Task("read book");
        task.markAsDone();

        task.unmarkAsDone();

        assertFalse(task.isDone());
    }

    @Test
    public void isDone_doneTask_agreesWithStatusIcon() {
        Task task = new Task("read book");
        task.markAsDone();

        // The two read the same flag, and the commands that record how to undo a
        // mark rely on the boolean saying what the icon shows.
        assertEquals("[X]", task.getStatusIcon());
        assertTrue(task.isDone());
    }

    // ---- display and save format ----

    @Test
    public void toString_notDoneTask_showsEmptyBoxThenDescription() {
        assertEquals("[ ] read book", new Task("read book").toString());
    }

    @Test
    public void toString_doneTask_showsTickedBox() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void toSaveFormat_notDoneTask_writesZeroThenDescription() {
        // The save format writes the completion state as a digit rather than as the icon, so the loader never has to
        // strip brackets, and stays separate from the display format so that restyling it cannot break loading.
        assertEquals("0 | read book", new Task("read book").toSaveFormat());
    }

    @Test
    public void toSaveFormat_doneTask_writesOne() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("1 | read book", task.toSaveFormat());
    }

    // ---- occursOn ----

    @Test
    public void occursOn_plainTask_isNeverOnAnyDay() {
        // A plain task carries no date, so it falls on no day. The dated subclasses override this, which is what lets
        // the on command filter the list without asking any task what type it is.
        Task task = new Task("read book");

        assertFalse(task.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(task.occursOn(LocalDate.of(1999, 1, 1)));
    }

    @Test
    public void occursOn_todo_isNeverOnAnyDay() {
        assertFalse(new TodoTask("read book").occursOn(LocalDate.of(2019, 12, 2)));
    }

    // ---- matches ----

    @Test
    public void matches_keywordInDescription_isTrue() {
        assertTrue(new Task("read book").matches("book"));
    }

    @Test
    public void matches_keywordNotInDescription_isFalse() {
        assertFalse(new Task("read book").matches("homework"));
    }

    /** The whole description counts as a match, not just a word inside it. */
    @Test
    public void matches_wholeDescription_isTrue() {
        assertTrue(new Task("read book").matches("read book"));
    }

    /** Substring, so a keyword need not sit on a word boundary. */
    @Test
    public void matches_keywordInsideWord_isTrue() {
        assertTrue(new Task("read bookmark").matches("book"));
    }

    @Test
    public void matches_differentCase_isFalse() {
        assertFalse(new Task("read Book").matches("book"));
    }

    /**
     * The match is on the description alone, never on how the task is displayed,
     * so a search cannot hit the type tag or the done marker. Searching a todo
     * for "T" would otherwise match every todo through its [T] tag.
     */
    @Test
    public void matches_typeTagOfDisplayedForm_isFalse() {
        Task todo = new TodoTask("read book");
        todo.markAsDone();

        assertTrue(todo.toString().contains("[T]"));
        assertFalse(todo.matches("[T]"));
        assertFalse(todo.matches("[X]"));
    }

    /** Likewise a deadline's formatted date is not searchable text. */
    @Test
    public void matches_formattedDateOfDeadline_isFalse() {
        Task deadline = new DeadlineTask("return book", LocalDateTime.of(2019, 12, 2, 18, 0));

        assertTrue(deadline.toString().contains("Dec 02 2019"));
        assertFalse(deadline.matches("Dec 02 2019"));
    }

    /** Every task matches the empty string, as every string contains it. */
    @Test
    public void matches_emptyKeyword_isTrue() {
        assertTrue(new Task("read book").matches(""));
    }

    // ---- TodoTask ----

    @Test
    public void todoToString_notDoneTodo_isTaggedT() {
        assertEquals("[T][ ] read book", new TodoTask("read book").toString());
    }

    @Test
    public void todoToString_doneTodo_isTaggedTAndTicked() {
        Task todo = new TodoTask("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void todoToSaveFormat_notDoneTodo_isTaggedT() {
        assertEquals("T | 0 | read book", new TodoTask("read book").toSaveFormat());
    }

    @Test
    public void todoToSaveFormat_doneTodo_writesTheDoneFlag() {
        Task todo = new TodoTask("read book");
        todo.markAsDone();

        assertTrue(todo.toSaveFormat().startsWith("T | 1 | "));
    }

    // ---- equality: what counts as the same task twice ----

    @Test
    public void equals_sameDescriptionSameType_isTrue() {
        assertEquals(new TodoTask("read book"), new TodoTask("read book"));
    }

    @Test
    public void equals_differentDescription_isFalse() {
        assertNotEquals(new TodoTask("read book"), new TodoTask("return book"));
    }

    @Test
    public void equals_oneDoneOneNot_isTrue() {
        // Ticking a task changes its state, not which task it is: adding "read book" again after finishing it is
        // still adding a duplicate.
        Task done = new TodoTask("read book");
        done.markAsDone();

        assertEquals(done, new TodoTask("read book"));
    }

    @Test
    public void equals_sameDescriptionDifferentType_isFalse() {
        // A todo and a deadline with the same text are different tasks, one dated and one not.
        assertNotEquals(new TodoTask("read book"), new DeadlineTask("read book", DEC_02_6PM));
    }

    @Test
    public void equals_deadlinesDueAtTheSameMoment_isTrue() {
        assertEquals(new DeadlineTask("read book", DEC_02_6PM), new DeadlineTask("read book", DEC_02_6PM));
    }

    @Test
    public void equals_deadlinesDueAtDifferentMoments_isFalse() {
        assertNotEquals(new DeadlineTask("read book", DEC_02_6PM),
                new DeadlineTask("read book", DEC_02_6PM.plusMinutes(1)));
    }

    @Test
    public void equals_eventsOverTheSameSpan_isTrue() throws ThomasException {
        assertEquals(new EventTask("meeting", DEC_02_6PM, DEC_02_6PM.plusHours(2)),
                new EventTask("meeting", DEC_02_6PM, DEC_02_6PM.plusHours(2)));
    }

    @Test
    public void equals_eventsDifferingOnlyInTheEnd_isFalse() throws ThomasException {
        // The end is compared as well as the start, so it is checked on its own.
        assertNotEquals(new EventTask("meeting", DEC_02_6PM, DEC_02_6PM.plusHours(2)),
                new EventTask("meeting", DEC_02_6PM, DEC_02_6PM.plusHours(3)));
    }

    @Test
    public void hashCode_equalTasks_isTheSame() {
        // The contract equals() carries with it, and what a hash-based lookup would depend on.
        assertEquals(new DeadlineTask("read book", DEC_02_6PM).hashCode(),
                new DeadlineTask("read book", DEC_02_6PM).hashCode());
    }

    /**
     * Both routes into a task refuse an empty description already, so a blank
     * one reaching the constructor means one of those checks was bypassed.
     * Asserted rather than thrown because it is a bug in the program, not a
     * mistake the user can make. Passes only with assertions enabled, which
     * the Gradle test task does by default.
     */
    @Test
    public void constructor_blankDescription_assertionThrown() {
        AssertionError error = assertThrows(AssertionError.class, () -> new TodoTask("   "));

        assertEquals("A task needs a description", error.getMessage());
    }
}
