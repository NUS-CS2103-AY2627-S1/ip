package billy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import billy.BillyException;

/**
 * Tests {@link Deadline}, a task that must be done by a particular moment.
 *
 * <p>The interesting method is {@link Deadline#occursOn(LocalDate)}, which is
 * what the {@code on} command uses to find the tasks belonging to a day. A
 * deadline falls on exactly one day, and the time of day must not affect that
 * answer: a deadline due at 6pm still belongs to that day and to no other.
 */
public class DeadlineTest {

    /** Builds a deadline due at the given typed date, for brevity below. */
    private static Deadline dueAt(String typedDate) throws BillyException {
        return new Deadline("return book", TaskDate.parse(typedDate));
    }

    @Test
    public void occursOn_theDayItIsDue_true() throws BillyException {
        assertTrue(dueAt("2019-12-02").occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_theDayItIsDueWithATime_true() throws BillyException {
        // The hour must not change which day the deadline belongs to.
        assertTrue(dueAt("2019-12-02 1800").occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_theDayBefore_false() throws BillyException {
        assertFalse(dueAt("2019-12-02").occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_theDayAfter_false() throws BillyException {
        // A deadline belongs to its own day only; it does not linger afterwards.
        assertFalse(dueAt("2019-12-02").occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_sameDayNumberDifferentMonth_false() throws BillyException {
        // Guards against comparing only the day of the month.
        assertFalse(dueAt("2019-12-02").occursOn(LocalDate.of(2019, 11, 2)));
    }

    @Test
    public void occursOn_sameDayAndMonthDifferentYear_false() throws BillyException {
        assertFalse(dueAt("2019-12-02").occursOn(LocalDate.of(2020, 12, 2)));
    }

    @Test
    public void toString_deadlineWithTime_dueMomentShown() throws BillyException {
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00pm)",
                dueAt("2019-12-02 1800").toString());
    }

    @Test
    public void toString_deadlineWithoutTime_onlyTheDayShown() throws BillyException {
        // No time was given, so none is shown; Billy does not claim midnight.
        assertEquals("[D][ ] return book (by: Dec 2 2019)", dueAt("2019-12-02").toString());
    }

    @Test
    public void toString_doneDeadline_ticked() throws BillyException {
        Deadline deadline = dueAt("2019-12-02");
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Dec 2 2019)", deadline.toString());
    }

    @Test
    public void toSaveFormat_deadlineWithTime_dueDateIsItsOwnField() throws BillyException {
        assertEquals("D | 0 | return book | 2019-12-02 1800",
                dueAt("2019-12-02 1800").toSaveFormat());
    }

    @Test
    public void toSaveFormat_deadlineWithoutTime_dateOnly() throws BillyException {
        assertEquals("D | 0 | return book | 2019-12-02", dueAt("2019-12-02").toSaveFormat());
    }

    @Test
    public void toSaveFormat_dayFirstInput_storedWithDashes() throws BillyException {
        // However the user typed the date, the file holds one format.
        assertEquals("D | 0 | return book | 2019-12-02", dueAt("2/12/2019").toSaveFormat());
    }

    @Test
    public void isSameTask_sameWordingAndSameDueDate_true() throws BillyException {
        assertTrue(new Deadline("essay", TaskDate.parse("2019-12-02"))
                .isSameTask(new Deadline("essay", TaskDate.parse("2019-12-02"))));
    }

    @Test
    public void isSameTask_sameWordingButDifferentDueDate_false() throws BillyException {
        // Two essays due on different days are two pieces of work that happen to
        // be worded alike, which is exactly what must not be called a duplicate.
        assertFalse(new Deadline("essay", TaskDate.parse("2019-12-02"))
                .isSameTask(new Deadline("essay", TaskDate.parse("2019-12-05"))));
    }

    @Test
    public void isSameTask_oneWithATimeAndOneWithout_false() throws BillyException {
        // A date with no time says nothing about the hour, so it is not the same
        // commitment as one due at a stated hour that day.
        assertFalse(new Deadline("essay", TaskDate.parse("2019-12-02"))
                .isSameTask(new Deadline("essay", TaskDate.parse("2019-12-02 1800"))));
    }
}
