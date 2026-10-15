package luckynoslacky.luckytask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

import org.junit.jupiter.api.Test;


/**
 * Tests the ToDo task subclass and the common task state behavior it inherits.
 */
class TodoTaskTest {

    /** Verifies the initial status and description of a ToDo. */
    @Test
    void todoTask_newTask_isNotDone() {
        TodoTask task = new TodoTask("read book");

        assertFalse(task.isDone());
        assertEquals("read book", task.getDescription());
    }

    /** Verifies that a ToDo formats its type marker and completion state. */
    @Test
    void todoTask_incompleteAndDoneStates_returnsTypedText() {
        TodoTask task = new TodoTask("read book");

        assertEquals("[📌][❗] read book", task.toString());
        task.markAsDone();
        assertEquals("[📌][✅] read book", task.toString());
    }

    /** Verifies null and blank descriptions use the exact validation message. */
    @Test
    void todoTask_nullOrBlankDescription_throwsExactException() {
        for (String description : new String[] {null, "", "   "}) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class, () -> new TodoTask(description));

            assertEquals("Task description cannot be empty.", exception.getMessage());
        }
    }

    /** Verifies that marking an incomplete ToDo sets it to done. */
    @Test
    void markAsDone_incompleteTask_isDone() {
        TodoTask task = new TodoTask("read book");

        task.markAsDone();

        assertTrue(task.isDone());
    }

    /** Verifies that unmarking a done ToDo clears its status. */
    @Test
    void unmarkAsUndone_doneTask_isNotDone() {
        TodoTask task = new TodoTask("read book");
        task.markAsDone();

        task.unmarkAsUndone();

        assertFalse(task.isDone());
    }

    /** Verifies that snoozing a ToDo is rejected. */
    @Test
    void snoozeBy_todoTask_throwsIllegalArgumentException() {
        TodoTask task = new TodoTask("read book");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                task.snoozeBy(new DurationPeriod(Period.ZERO, Duration.ofHours(1))));

        assertEquals("ToDos cannot be snoozed.", exception.getMessage());
    }

    /** Verifies that replacing a ToDo ending time is rejected. */
    @Test
    void reschedule_todoTask_throwsIllegalArgumentException() {
        TodoTask task = new TodoTask("read book");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                task.reschedule(TaskTimes.makeDeadlineTimes(
                        LocalDateTime.of(2026, 8, 26, 14, 0))));

        assertEquals("ToDos cannot be rescheduled.", exception.getMessage());
    }

    /** Verifies that requesting a ToDo ending time is rejected. */
    @Test
    void getEndTime_todoTask_throwsIllegalArgumentException() {
        TodoTask task = new TodoTask("read book");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, task::getEndTime);

        assertEquals("Task has no ending time.", exception.getMessage());
    }

    /** Verifies that a ToDo rejects scheduling validation with its domain reason. */
    @Test
    void verifyCanBeScheduled_todoTask_throwsTodoTaskReason() {
        TodoTask task = new TodoTask("read book");

        TaskSchedulingException exception = assertThrows(
                TaskSchedulingException.class, task::verifyCanBeScheduled);

        assertEquals(TaskSchedulingException.Reason.TODO_TASK, exception.getReason());
    }

    /** Verifies that a ToDo rejects creation of snoozed timing information. */
    @Test
    void createSnoozedTimes_todoTask_throwsTodoTaskReason() {
        TodoTask task = new TodoTask("read book");

        TaskSchedulingException exception = assertThrows(
                TaskSchedulingException.class, () -> task.createSnoozedTimes(
                        new DurationPeriod(Period.ZERO, Duration.ofHours(1))));

        assertEquals(TaskSchedulingException.Reason.TODO_TASK, exception.getReason());
    }

    /** Verifies that a ToDo rejects creation of replacement timing information. */
    @Test
    void createRescheduledTimes_todoTask_throwsTodoTaskReason() {
        TodoTask task = new TodoTask("read book");

        TaskSchedulingException exception = assertThrows(
                TaskSchedulingException.class, () -> task.createRescheduledTimes(
                        null,
                        LocalDateTime.of(2026, 8, 26, 14, 0),
                        LocalDateTime.of(2026, 8, 26, 12, 0)));

        assertEquals(TaskSchedulingException.Reason.TODO_TASK, exception.getReason());
    }
}
