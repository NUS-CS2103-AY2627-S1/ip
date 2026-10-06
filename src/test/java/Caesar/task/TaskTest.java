package caesar.task;

import caesar.exception.CaesarException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskTest {

    @Test
    public void testMarkAsDoneChangesPendingTaskToCompleted() throws CaesarException {
        Task task = new Task("Task");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("[X] Task", task.toString());
    }

    @Test
    public void testMarkAsNotDoneChangesCompletedTaskToPending() throws CaesarException {
        Task task = new Task("Task");
        task.markAsDone();

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals("[ ] Task", task.toString());
    }

    @Test
    public void testMarkAsNotDoneRejectsPendingTask() {
        Task task = new Task("Task");

        CaesarException exception = assertThrows(
                CaesarException.class,
                task::markAsNotDone
        );

        assertEquals("You have unmarked this task!", exception.getMessage());
        assertTrue(!task.isDone());
    }
}
