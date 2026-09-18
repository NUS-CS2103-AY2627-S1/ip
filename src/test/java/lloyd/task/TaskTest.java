package lloyd.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the completion state shared by all {@link Task} objects.
 */
public class TaskTest {

    /** Verifies that a newly created task starts incomplete. */
    @Test
    public void task_newTask_isNotDone() {
        Task task = new Task("Test task");
        assertFalse(task.isDone());
    }

    /** Verifies that marking a task changes its state to complete. */
    @Test
    public void task_markTask_isDone() {
        Task task = new Task("Test task 2");
        task.mark();
        assertTrue(task.isDone());
    }

    /** Verifies that unmarking a completed task restores its incomplete state. */
    @Test
    public void task_unmarkTask_isNotDone() {
        Task task = new Task("Test task 3");
        task.mark();
        task.unmark();
        assertFalse(task.isDone());
    }
}
