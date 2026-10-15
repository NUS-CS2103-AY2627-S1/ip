package sage.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void task_defaultsToNotDoneAndFormatsDescription() {
        Task task = new Task("read book", TaskType.TODO);

        assertEquals(" ", task.getStatusIcon());
        assertEquals(TaskType.TODO, task.getType());
        assertEquals("read book", task.getDescription());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void markOperationsToggleCompletionState() {
        Task task = new Task("submit report", TaskType.DEADLINE);

        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("[D][X] submit report", task.toString());

        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertFalse(task.toString().contains("[X]"));
        assertTrue(task.toString().contains("[ ]"));
    }
}
