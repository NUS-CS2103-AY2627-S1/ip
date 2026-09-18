package bobby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the state and formatting behaviour of {@link Task} objects.
 */
class TaskTest {
    /**
     * Verifies that an uncompleted task is saved with an incomplete status.
     */
    @Test
    void toFileString_newTask_incompleteRecordReturned() {
        Task task = new Task("read book");

        assertEquals("T | 0 | read book | -", task.toFileString());
    }

    /**
     * Verifies that a completed task is saved with a completed status.
     */
    @Test
    void toFileString_completedTask_completeRecordReturned() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("T | 1 | read book | -", task.toFileString());
    }

    /**
     * Verifies that completion timestamps are truncated, preserved, cleared, and replaced as specified.
     */
    @Test
    void completionDateTime_statusChanges_expectedCompletionDateTimeStored() {
        Task task = new Task("read book");
        LocalDateTime firstCompletion = LocalDateTime.of(2026, 9, 8, 10, 15, 30, 123_000_000);
        LocalDateTime secondCompletion = LocalDateTime.of(2026, 9, 9, 11, 20, 45);

        task.markAsDone(firstCompletion);
        task.markAsDone(secondCompletion);

        assertEquals("T | 1 | read book | 2026-09-08T10:15:30", task.toFileString());
        assertEquals(true, task.hasKnownCompletionDateTime());

        task.markAsNotDone();

        assertEquals("T | 0 | read book | -", task.toFileString());
        assertEquals(false, task.hasKnownCompletionDateTime());

        task.markAsDone(secondCompletion);

        assertEquals("T | 1 | read book | 2026-09-09T11:20:45", task.toFileString());
    }

    /**
     * Verifies that tasks reject missing and blank descriptions.
     */
    @Test
    void constructor_invalidDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Task(""));
        assertThrows(AssertionError.class, () -> new Task("   "));
        assertThrows(AssertionError.class, () -> new Task(null));
    }

    /**
     * Verifies that a task retains the description supplied at creation.
     */
    @Test
    void getDescription_taskCreated_originalDescriptionReturned() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
    }

    /**
     * Verifies that completing and uncompleting a task changes its status icon.
     */
    @Test
    void statusMethods_taskStateChanges_expectedIconsReturned() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
        assertFalse(task.isDone());

        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertTrue(task.isDone());

        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertFalse(task.isDone());
    }

    /**
     * Verifies that marking an already completed task does not replace its original completion time.
     */
    @Test
    void markAsDone_alreadyCompletedTask_originalCompletionDateTimePreserved() {
        Task task = new Task("read book");
        LocalDateTime originalCompletion = LocalDateTime.of(2026, 9, 8, 10, 15, 30);

        task.markAsDone(originalCompletion);
        task.markAsDone(LocalDateTime.of(2026, 9, 9, 11, 20, 45));
        task.markAsDone();

        assertEquals(originalCompletion, task.getCompletionDateTime().orElseThrow());
    }

    /**
     * Verifies that the timestamped completion method rejects a missing date and time.
     */
    @Test
    void markAsDone_nullCompletionDateTime_assertionErrorThrown() {
        Task task = new Task("read book");

        assertThrows(AssertionError.class, () -> task.markAsDone(null));
    }

    /**
     * Verifies that task-detail comparison ignores case and repeated whitespace but respects task type.
     */
    @Test
    void hasSameDetailsAs_variedTasks_expectedComparisonReturned() {
        Task task = new Todo("  Read   book ");

        assertTrue(task.hasSameDetailsAs(new Todo("read book")));
        assertFalse(task.hasSameDetailsAs(new Task("read book")));
        assertFalse(task.hasSameDetailsAs(new Todo("write essay")));
        assertFalse(task.hasSameDetailsAs(null));
    }

    /**
     * Verifies that deadlines are equal only when their normalized descriptions and dates match.
     */
    @Test
    void deadlineHasSameDetailsAs_variedDeadlines_expectedComparisonReturned() {
        LocalDateTime deadlineDateTime = LocalDateTime.of(2026, 9, 1, 14, 0);
        Deadline deadline = new Deadline("Return book", deadlineDateTime);

        assertTrue(deadline.hasSameDetailsAs(new Deadline("return book", deadlineDateTime)));
        assertFalse(deadline.hasSameDetailsAs(new Deadline(
                "return book", deadlineDateTime.plusMinutes(1))));
        assertFalse(deadline.hasSameDetailsAs(new Todo("return book")));
    }

    /**
     * Verifies that events are equal only when their normalized descriptions and full ranges match.
     */
    @Test
    void eventHasSameDetailsAs_variedEvents_expectedComparisonReturned() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 1, 14, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2026, 9, 1, 16, 0);
        Event event = new Event("Project meeting", startDateTime, endDateTime);

        assertTrue(event.hasSameDetailsAs(new Event(
                "project meeting", startDateTime, endDateTime)));
        assertFalse(event.hasSameDetailsAs(new Event(
                "project meeting", startDateTime.plusMinutes(1), endDateTime)));
        assertFalse(event.hasSameDetailsAs(new Event(
                "project meeting", startDateTime, endDateTime.plusMinutes(1))));
        assertFalse(event.hasSameDetailsAs(new Todo("project meeting")));
    }

    /**
     * Verifies that completion-range checks use an inclusive start and exclusive end.
     */
    @Test
    void wasCompletedBetween_boundaryAndIncompleteTasks_expectedResultsReturned() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 7, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2026, 9, 14, 0, 0);
        Task task = new Task("read book");

        assertFalse(task.wasCompletedBetween(startDateTime, endDateTime));

        task.markAsDone(startDateTime);
        assertTrue(task.wasCompletedBetween(startDateTime, endDateTime));

        task.markAsNotDone();
        task.markAsDone(endDateTime);
        assertFalse(task.wasCompletedBetween(startDateTime, endDateTime));

        task.markAsNotDone();
        task.markAsDone();
        assertFalse(task.wasCompletedBetween(startDateTime, endDateTime));
    }

    /**
     * Verifies that completion-range checks reject missing or non-increasing interval bounds.
     */
    @Test
    void wasCompletedBetween_invalidInterval_assertionErrorThrown() {
        Task task = new Task("read book");
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 7, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2026, 9, 14, 0, 0);

        assertThrows(AssertionError.class, () -> task.wasCompletedBetween(null, endDateTime));
        assertThrows(AssertionError.class, () -> task.wasCompletedBetween(startDateTime, null));
        assertThrows(AssertionError.class, () ->
                task.wasCompletedBetween(startDateTime, startDateTime));
        assertThrows(AssertionError.class, () ->
                task.wasCompletedBetween(endDateTime, startDateTime));
    }

    /**
     * Verifies that an incomplete task uses the standard display format.
     */
    @Test
    void toString_newTask_standardIncompleteFormatReturned() {
        Task task = new Task("read book");

        assertEquals("[?][ ] read book", task.toString());
    }

    /**
     * Verifies that a completed task includes its completed status in the display format.
     */
    @Test
    void toString_completedTask_standardCompleteFormatReturned() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("[?][X] read book", task.toString());
    }

    /**
     * Verifies that a deadline labels and formats its deadline date and time.
     */
    @Test
    void toString_deadlineTask_formattedDeadlineReturned() {
        LocalDateTime deadlineDateTime = LocalDateTime.of(2026, 9, 1, 14, 0);
        Deadline deadline = new Deadline("return book", deadlineDateTime);

        assertEquals("[D][ ] return book (appointed for: Sep 01 2026 2:00 PM)", deadline.toString());
        assertEquals("D | 0 | return book | 2026-09-01T14:00 | -", deadline.toFileString());
    }

    /**
     * Verifies that an event labels and formats its start and end date-times.
     */
    @Test
    void toString_eventTask_formattedStartAndEndReturned() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 1, 14, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2026, 9, 1, 16, 0);
        Event event = new Event("meeting", startDateTime, endDateTime);

        assertEquals("[E][ ] meeting (commencing: Sep 01 2026 2:00 PM; concluding: Sep 01 2026 4:00 PM)",
                event.toString());
        assertEquals("E | 0 | meeting | 2026-09-01T14:00 | 2026-09-01T16:00 | -",
                event.toFileString());
    }

    /**
     * Verifies that date-based tasks reject missing date-time values.
     */
    @Test
    void dateBasedTask_nullDateTime_assertionErrorThrown() {
        LocalDateTime validDateTime = LocalDateTime.of(2026, 9, 1, 14, 0);

        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
        assertThrows(AssertionError.class, () -> new Event("meeting", null, validDateTime));
        assertThrows(AssertionError.class, () -> new Event("meeting", validDateTime, null));
    }

    /**
     * Verifies that an event constructor enforces a strictly increasing time range.
     */
    @Test
    void event_invalidTimeRange_assertionErrorThrown() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 1, 14, 0);

        assertThrows(AssertionError.class, () ->
                new Event("meeting", startDateTime, startDateTime));
        assertThrows(AssertionError.class, () ->
                new Event("meeting", startDateTime, startDateTime.minusMinutes(1)));
    }
}
