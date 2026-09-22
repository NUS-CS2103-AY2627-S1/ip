package lebron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Task#fromFileFormat(String)} -- the factory that rebuilds
 * a task (and its subclass) from one data-file line, and that is expected to
 * reject a corrupted line by throwing. Also covers the shared done-status
 * behaviour on the base class.
 */
public class TaskTest {

    // ---- fromFileFormat: happy paths ----------------------------------------

    @Test
    public void fromFileFormat_todoLine_returnsUndoneTodo() {
        Task task = Task.fromFileFormat("T | 0 | read book");
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void fromFileFormat_todoLineDone_returnsDoneTodo() {
        Task task = Task.fromFileFormat("T | 1 | read book");
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void fromFileFormat_deadlineLine_returnsDeadlineWithFormattedDate() {
        Task task = Task.fromFileFormat("D | 0 | return book | 2019-12-02");
        assertEquals("[D][ ] return book (by: Dec 02 2019)", task.toString());
    }

    @Test
    public void fromFileFormat_deadlineLineWithTime_returnsDeadlineWithTime() {
        Task task = Task.fromFileFormat("D | 1 | submit report | 2019-12-02 1800");
        assertEquals("[D][X] submit report (by: Dec 02 2019 6:00pm)", task.toString());
    }

    @Test
    public void fromFileFormat_eventLine_returnsEventWithBothEndpoints() {
        Task task = Task.fromFileFormat("E | 0 | camp | 2019-06-01 | 2019-06-03");
        assertEquals("[E][ ] camp (from: Jun 01 2019 to: Jun 03 2019)", task.toString());
    }

    @Test
    public void fromFileFormat_extraSurroundingSpaces_stillParses() {
        Task task = Task.fromFileFormat("T |   1   |   read book  ");
        assertEquals("[X] read book", task.toString());
    }

    // ---- round-trips: toFileFormat then back -------------------------------

    @Test
    public void roundTrip_todo_preservesLineAndDisplay() {
        Task original = new Todo("read book");
        original.markAsDone();
        Task restored = Task.fromFileFormat(original.toFileFormat());
        assertEquals(original.toFileFormat(), restored.toFileFormat());
        assertEquals(original.toString(), restored.toString());
    }

    @Test
    public void roundTrip_deadlineWithTime_preservesLine() {
        Task original = new Deadline("submit report", DateTime.parse("2/12/2019 1800"));
        Task restored = Task.fromFileFormat(original.toFileFormat());
        assertEquals("D | 0 | submit report | 2019-12-02 1800", restored.toFileFormat());
    }

    @Test
    public void roundTrip_event_preservesLine() {
        Task original = new Event("camp", DateTime.parse("2019-06-01"), DateTime.parse("2019-06-03"));
        Task restored = Task.fromFileFormat(original.toFileFormat());
        assertEquals(original.toFileFormat(), restored.toFileFormat());
    }

    // ---- fromFileFormat: corrupted lines ----------------------------------

    @Test
    public void fromFileFormat_tooFewFields_throws() {
        assertThrows(IllegalArgumentException.class, () -> Task.fromFileFormat("T | 0"));
    }

    @Test
    public void fromFileFormat_unknownType_throws() {
        assertThrows(IllegalArgumentException.class, () -> Task.fromFileFormat("X | 0 | mystery"));
    }

    @Test
    public void fromFileFormat_badDoneFlag_throws() {
        assertThrows(IllegalArgumentException.class, () -> Task.fromFileFormat("T | 2 | read book"));
    }

    @Test
    public void fromFileFormat_emptyDescription_throws() {
        assertThrows(IllegalArgumentException.class, () -> Task.fromFileFormat("T | 0 | "));
    }

    @Test
    public void fromFileFormat_todoWithTrailingDateField_throws() {
        // A todo line must have exactly 3 fields.
        assertThrows(IllegalArgumentException.class, () ->
                Task.fromFileFormat("T | 0 | read book | 2019-12-02"));
    }

    @Test
    public void fromFileFormat_deadlineMissingDateField_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                Task.fromFileFormat("D | 0 | return book"));
    }

    @Test
    public void fromFileFormat_deadlineUnparseableDate_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                Task.fromFileFormat("D | 0 | return book | someday"));
    }

    // ---- fromFileFormat: recurrence (B-RecurringTasks) --------------------

    @Test
    public void fromFileFormat_deadlineWithRecurrence_returnsRecurringDeadline() {
        Task task = Task.fromFileFormat("D | 0 | pay rent | 2025-01-01 | month");
        assertEquals("[D][ ] pay rent (by: Jan 01 2025) (every: month)", task.toString());
    }

    @Test
    public void fromFileFormat_eventWithRecurrence_returnsRecurringEvent() {
        Task task = Task.fromFileFormat("E | 0 | standup | 2025-01-06 0900 | 2025-01-06 0930 | week");
        assertEquals("[E][ ] standup (from: Jan 06 2025 9:00am to: Jan 06 2025 9:30am) (every: week)",
                task.toString());
    }

    @Test
    public void fromFileFormat_deadlineWithoutRecurrence_isBackwardCompatible() {
        // Pre-existing 4-field D lines (no recurrence) must still load fine.
        Task task = Task.fromFileFormat("D | 0 | return book | 2019-12-02");
        assertEquals("[D][ ] return book (by: Dec 02 2019)", task.toString());
    }

    @Test
    public void fromFileFormat_eventWithoutRecurrence_isBackwardCompatible() {
        // Pre-existing 5-field E lines (no recurrence) must still load fine.
        Task task = Task.fromFileFormat("E | 0 | camp | 2019-06-01 | 2019-06-03");
        assertEquals("[E][ ] camp (from: Jun 01 2019 to: Jun 03 2019)", task.toString());
    }

    @Test
    public void fromFileFormat_deadlineUnrecognisedRecurrence_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                Task.fromFileFormat("D | 0 | pay rent | 2025-01-01 | fortnight"));
    }

    @Test
    public void fromFileFormat_deadlineTooManyFields_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                Task.fromFileFormat("D | 0 | pay rent | 2025-01-01 | month | extra"));
    }

    @Test
    public void roundTrip_recurringDeadline_preservesLineAndDisplay() {
        Task original = new Deadline("pay rent", DateTime.parse("2025-01-01"), RecurrencePeriod.MONTH);
        Task restored = Task.fromFileFormat(original.toFileFormat());
        assertEquals(original.toFileFormat(), restored.toFileFormat());
        assertEquals(original.toString(), restored.toString());
    }

    @Test
    public void roundTrip_recurringEvent_preservesLine() {
        Task original = new Event("standup",
                DateTime.parse("2025-01-06 0900"), DateTime.parse("2025-01-06 0930"), RecurrencePeriod.WEEK);
        Task restored = Task.fromFileFormat(original.toFileFormat());
        assertEquals(original.toFileFormat(), restored.toFileFormat());
    }

    // ---- base class done-status behaviour --------------------------------

    @Test
    public void markAsDone_thenNotDone_statusIconTracksState() {
        Task task = new Todo("read book");
        assertEquals(" ", task.getStatusIcon());
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }
}
