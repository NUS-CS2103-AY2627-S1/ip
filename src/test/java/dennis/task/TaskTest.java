package dennis.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import dennis.DennisException;

/**
 * Tests for the behaviour that lives in {@link Task} itself: date parsing and
 * formatting, the save-file separator guard, completion-status tracking, the
 * text form of a task, and the default {@link Task#occursOn(LocalDate)}.
 *
 * <p>Most of these are pure functions (same input, same result), which makes
 * them straightforward to unit test. The few that depend on instance state
 * are exercised through {@link TaskStub}, a minimal concrete subclass, because
 * {@code Task} is abstract.</p>
 */
public class TaskTest {

    /**
     * Smallest possible concrete {@link Task}, so the instance-level methods
     * (mark/unmark, status icon, {@code toString}) can be called directly.
     */
    private static final class TaskStub extends Task {
        private TaskStub(String description) {
            super(description);
        }

        @Override
        public String toFileFormat() {
            return "STUB";
        }
    }

    // ------------------------------------------------------------------
    // parseDate(String, String)
    // ------------------------------------------------------------------

    @Test
    public void parseDate_isoDate_returnsThatDate() throws DennisException {
        assertEquals(LocalDate.of(2019, 12, 1),
                Task.parseDate("2019-12-01", "The date"));
    }

    @Test
    public void parseDate_surroundingWhitespace_returnsTrimmedDate()
            throws DennisException {
        // isBlank() lets it through and LocalDate.parse sees the trimmed text.
        assertEquals(LocalDate.of(2019, 12, 1),
                Task.parseDate("  2019-12-01  ", "The date"));
    }

    @Test
    public void parseDate_leapDayInLeapYear_returnsThatDate()
            throws DennisException {
        assertEquals(LocalDate.of(2020, 2, 29),
                Task.parseDate("2020-02-29", "The date"));
    }

    @Test
    public void parseDate_emptyString_messageNamesFieldAndSaysEmpty() {
        DennisException e = assertThrows(DennisException.class, () ->
                Task.parseDate("", "The deadline"));
        assertEquals("The deadline cannot be empty.", e.getMessage());
    }

    @Test
    public void parseDate_whitespaceOnly_throwsDennisException() {
        assertThrows(DennisException.class, () ->
                Task.parseDate("   ", "The date"));
    }

    @Test
    public void parseDate_missingZeroPadding_throwsDennisException() {
        // "2019-1-5" is rejected; ISO form requires yyyy-MM-dd exactly.
        assertThrows(DennisException.class, () ->
                Task.parseDate("2019-1-5", "The date"));
    }

    @Test
    public void parseDate_slashSeparators_throwsDennisException() {
        assertThrows(DennisException.class, () ->
                Task.parseDate("2019/12/01", "The date"));
    }

    @Test
    public void parseDate_dayMonthYearOrder_throwsDennisException() {
        assertThrows(DennisException.class, () ->
                Task.parseDate("01-12-2019", "The date"));
    }

    @Test
    public void parseDate_nonDateText_messageShowsExpectedFormat() {
        DennisException e = assertThrows(DennisException.class, () ->
                Task.parseDate("tomorrow", "The date"));
        assertEquals(
                "The date must be a date in yyyy-MM-dd form, e.g. 2019-12-01.",
                e.getMessage());
    }

    @Test
    public void parseDate_monthAboveTwelve_throwsDennisException() {
        assertThrows(DennisException.class, () ->
                Task.parseDate("2019-13-01", "The date"));
    }

    @Test
    public void parseDate_dayBeyondMonthLength_throwsDennisException() {
        // April has 30 days, so the 31st is not a real date.
        assertThrows(DennisException.class, () ->
                Task.parseDate("2019-04-31", "The date"));
    }

    @Test
    public void parseDate_leapDayInNonLeapYear_throwsDennisException() {
        // 2019 is not a leap year, so Feb 29 does not exist.
        assertThrows(DennisException.class, () ->
                Task.parseDate("2019-02-29", "The date"));
    }

    // ------------------------------------------------------------------
    // formatDate(LocalDate)
    // ------------------------------------------------------------------

    @Test
    public void formatDate_typicalDate_returnsMonthDayYear() {
        assertEquals("Dec 01 2019",
                Task.formatDate(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void formatDate_singleDigitDay_padsDayToTwoDigits() {
        assertEquals("Jan 05 2020",
                Task.formatDate(LocalDate.of(2020, 1, 5)));
    }

    @Test
    public void formatDate_nonEnglishDefaultLocale_stillUsesEnglishMonth() {
        // The formatter pins Locale.ENGLISH; changing the JVM default must
        // not change the month abbreviation.
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            assertEquals("Oct 15 2021",
                    Task.formatDate(LocalDate.of(2021, 10, 15)));
        } finally {
            Locale.setDefault(original);
        }
    }

    // ------------------------------------------------------------------
    // rejectSeparator(String, String)
    // ------------------------------------------------------------------

    @Test
    public void rejectSeparator_valueWithoutPipe_returnsSameValue()
            throws DennisException {
        String value = "read book";
        assertSame(value, Task.rejectSeparator(value, "A task description"));
    }

    @Test
    public void rejectSeparator_emptyValue_returnsEmptyValue()
            throws DennisException {
        assertEquals("", Task.rejectSeparator("", "A task description"));
    }

    @Test
    public void rejectSeparator_valueContainingPipe_throwsDennisException() {
        assertThrows(DennisException.class, () ->
                Task.rejectSeparator("chapter 1 | 2", "A task description"));
    }

    @Test
    public void rejectSeparator_pipe_messageNamesField() {
        DennisException e = assertThrows(DennisException.class, () ->
                Task.rejectSeparator("a|b", "A task description"));
        assertEquals("A task description cannot contain the '|' character.",
                e.getMessage());
    }

    // ------------------------------------------------------------------
    // markAsDone / markAsNotDone / getStatusIcon / getStatusNumber
    // ------------------------------------------------------------------

    @Test
    public void getStatusIcon_newTask_returnsBlank() {
        assertEquals(" ", new TaskStub("read book").getStatusIcon());
    }

    @Test
    public void getStatusNumber_newTask_returnsZero() {
        assertEquals("0", new TaskStub("read book").getStatusNumber());
    }

    @Test
    public void markAsDone_marksTaskDone() {
        TaskStub task = new TaskStub("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("1", task.getStatusNumber());
    }

    @Test
    public void markAsNotDone_revertsADoneTask() {
        TaskStub task = new TaskStub("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertEquals("0", task.getStatusNumber());
    }

    // ------------------------------------------------------------------
    // toString()
    // ------------------------------------------------------------------

    @Test
    public void toString_newTask_showsEmptyBoxAndDescription() {
        assertEquals("[ ] read book", new TaskStub("read book").toString());
    }

    @Test
    public void toString_doneTask_showsCheckedBoxAndDescription() {
        TaskStub task = new TaskStub("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
    }

    // ------------------------------------------------------------------
    // occursOn(LocalDate)
    // ------------------------------------------------------------------

    @Test
    public void occursOn_plainTask_isAlwaysFalse() {
        // Only dated task types (Deadline, Event) override this to return true.
        assertFalse(new TaskStub("read book").occursOn(LocalDate.of(2019, 12, 1)));
    }

    // ------------------------------------------------------------------
    // matches(String)
    // ------------------------------------------------------------------

    @Test
    public void matches_keywordInDescription_returnsTrue() {
        assertTrue(new TaskStub("read book").matches("book"));
    }

    @Test
    public void matches_keywordAbsent_returnsFalse() {
        assertFalse(new TaskStub("read book").matches("magazine"));
    }

    @Test
    public void matches_partialWord_returnsTrue() {
        // The keyword is a plain substring, not a whole-word match.
        assertTrue(new TaskStub("read book").matches("boo"));
    }

    @Test
    public void matches_differentCase_returnsFalse() {
        // Matching is case-sensitive.
        assertFalse(new TaskStub("read book").matches("Book"));
    }
}
