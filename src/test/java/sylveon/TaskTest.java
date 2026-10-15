package sylveon;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the completion state of tasks. */
public class TaskTest {
    @Test
    public void markAsDone_newTask_taskBecomesDone() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        task.markAsDone();
        assertTrue(task.isDone());
    }

    @Test
    public void markAsNotDone_completedTask_taskBecomesNotDone() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertFalse(task.isDone());
    }
}
