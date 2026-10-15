package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import bob.BobException;

/**
 * Tests {@link Deadline}, the task pinned to a single point in time.
 *
 * <p>A deadline adds little to {@link Task}, so what is checked here is mostly
 * that it takes the inherited date questions at their word: unlike an
 * {@link Event}, it covers no stretch of time, so it falls on exactly one day
 * and is before or after every other. The display and save forms are checked too,
 * since both are contracts other parts of the chatbot depend on — the save form
 * is what {@link bob.storage.Storage Storage} has to be able to read back.
 */
public class DeadlineTest {

    @Test
    public void getTypeIcon_anyDeadline_returnsD() throws BobException {
        assertEquals("D", deadlineOn("2026-12-02").getTypeIcon());
        assertEquals(Deadline.TYPE_ICON, deadlineOn("2026-12-02").getTypeIcon());
    }

    @Test
    public void getScheduledDate_anyDeadline_returnsTheDueDate() throws BobException {
        Deadline deadline = deadlineOn("2026-12-02 1800");

        assertEquals("2026-12-02 1800", deadline.getScheduledDate().orElseThrow().toSaveField());
    }

    @Test
    public void occursOn_theDueDay_true() throws BobException {
        Deadline deadline = deadlineOn("2026-12-02");

        assertTrue(deadline.occursOn(LocalDate.of(2026, 12, 2)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 12, 1)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 12, 3)));
    }

    @Test
    public void occursOn_dueDayWithATimeOnIt_stillThatDay() throws BobException {
        // The time of day does not change which day the task falls on.
        assertTrue(deadlineOn("2026-12-02 1800").occursOn(LocalDate.of(2026, 12, 2)));
    }

    @Test
    public void dateChecks_dueDayItself_neitherBeforeNorAfter() throws BobException {
        Deadline deadline = deadlineOn("2026-12-02");
        LocalDate dueDay = LocalDate.of(2026, 12, 2);

        assertFalse(deadline.isBefore(dueDay));
        assertFalse(deadline.isAfter(dueDay));
    }

    @Test
    public void dateChecks_daysEitherSide_beforeAndAfter() throws BobException {
        Deadline deadline = deadlineOn("2026-12-02");

        assertTrue(deadline.isBefore(LocalDate.of(2026, 12, 3)));
        assertTrue(deadline.isAfter(LocalDate.of(2026, 12, 1)));
    }

    @Test
    public void toString_newDeadline_showsTheDueDateInTheFriendlyForm() throws BobException {
        assertEquals("[D][ ] return book (by: Dec 02 2026)", deadlineOn("2026-12-02").toString());
    }

    @Test
    public void toString_dueDateWithATime_timeShown() throws BobException {
        assertEquals("[D][ ] return book (by: Dec 02 2026 18:00)",
                deadlineOn("2026-12-02 1800").toString());
    }

    @Test
    public void toString_doneDeadline_showsCrossInStatusBox() throws BobException {
        Deadline deadline = deadlineOn("2026-12-02");
        deadline.markAsDone();

        assertEquals("[D][X] return book (by: Dec 02 2026)", deadline.toString());
    }

    @Test
    public void toSaveFields_anyDeadline_addsTheDueDateAsTheFourthField() throws BobException {
        // Saved as the user types it, which is the form TaskDateTime reads back.
        assertEquals(List.of("D", Task.NOT_DONE_FLAG, "return book", "2026-12-02"),
                deadlineOn("2026-12-02").toSaveFields());
    }

    @Test
    public void toSaveFields_doneDeadline_returnsDoneFlag() throws BobException {
        Deadline deadline = deadlineOn("2026-12-02 1800");
        deadline.markAsDone();

        assertEquals(List.of("D", Task.DONE_FLAG, "return book", "2026-12-02 1800"),
                deadline.toSaveFields());
    }

    @Test
    public void withEdit_newDueDate_dueDateChanged() throws BobException {
        Task edited = deadlineOn("2026-12-02").withEdit(dueDateEdit("2026-12-05"));

        assertEquals("[D][ ] return book (by: Dec 05 2026)", edited.toString());
    }

    @Test
    public void withEdit_dayWithoutATime_timeRemoved() throws BobException {
        Task edited = deadlineOn("2026-12-02 1800").withEdit(dueDateEdit("2026-12-05"));

        // The due date is replaced whole, so leaving the time off does not keep the old one.
        assertEquals("[D][ ] return book (by: Dec 05 2026)", edited.toString());
    }

    @Test
    public void withEdit_descriptionOnly_dueDateKept() throws BobException {
        TaskEdit edit = new TaskEdit("return library book", null, null, null);

        Task edited = deadlineOn("2026-12-02 1800").withEdit(edit);

        assertEquals("[D][ ] return library book (by: Dec 02 2026 18:00)", edited.toString());
    }

    @Test
    public void withEdit_startOrEnd_exceptionThrown() throws BobException {
        Deadline deadline = deadlineOn("2026-12-02");
        TaskDateTime date = TaskDateTime.parse("2026-12-05");

        BobException exception = assertThrows(BobException.class, () ->
                deadline.withEdit(new TaskEdit(null, null, date, null)));

        assertEquals("A deadline has a due date, not a start or an end.", exception.getMessage());
        // Refused as a whole, even alongside a due date the deadline could have taken.
        assertThrows(BobException.class, () -> deadline.withEdit(new TaskEdit(null, date, null, date)));
    }

    @Test
    public void isSameTaskAs_sameDueDateDescriptionInOtherCapitals_true() throws BobException {
        Deadline retyped = new Deadline("Return  Book", TaskDateTime.parse("2026-12-02 1800"));

        assertTrue(deadlineOn("2026-12-02 1800").isSameTaskAs(retyped));
    }

    @Test
    public void isSameTaskAs_differentDueDate_false() throws BobException {
        Deadline deadline = deadlineOn("2026-12-02");

        assertFalse(deadline.isSameTaskAs(deadlineOn("2026-12-03")));
        // Due on the day and due at midnight on it were written differently.
        assertFalse(deadline.isSameTaskAs(deadlineOn("2026-12-02 0000")));
    }

    /** Returns a deadline called {@code return book} due at {@code date}. */
    private static Deadline deadlineOn(String date) throws BobException {
        return new Deadline("return book", TaskDateTime.parse(date));
    }

    /** Returns an edit that changes nothing but the due date, to {@code date}. */
    private static TaskEdit dueDateEdit(String date) throws BobException {
        return new TaskEdit(null, TaskDateTime.parse(date), null, null);
    }
}
