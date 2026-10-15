package luckynoslacky.luckytask;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the task completion status representations.
 */
class TaskStatusTest {

    /** Verifies status transitions behave consistently for every task type. */
    @Test
    void taskStatus_allTaskTypes_supportIdempotentTransitions() {
        List<Task> tasks = List.of(
                new TodoTask("read book"),
                new DeadlineTask("return book", LocalDateTime.of(2026, 8, 26, 12, 0)),
                new EventTask(
                        "project meeting",
                        LocalDateTime.of(2026, 8, 26, 14, 0),
                        LocalDateTime.of(2026, 8, 26, 16, 0)));

        for (Task task : tasks) {
            assertFalse(task.isDone());
            task.markAsDone();
            task.markAsDone();
            assertTrue(task.isDone());
            task.unmarkAsUndone();
            task.unmarkAsUndone();
            assertFalse(task.isDone());
        }
    }
}
