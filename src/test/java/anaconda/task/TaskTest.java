package anaconda.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests task status transitions and the base task's display representation.
 */
public class TaskTest {
    @Test
    public void constructor_description_createsIncompleteTask() {
        Task task = new Task("Read  Book");
        assertEquals("Read  Book", task.getDescription());
        assertFalse(task.isDone());
        assertNull(task.getEndDate());
        assertEquals("[ ] Read  Book", task.toString());
    }

    @Test
    public void markAsDone_repeatedCalls_keepsTaskCompleted() {
        Task task = new Task("book");
        task.markAsDone();
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("[X] book", task.toString());
        assertEquals("book", task.getDescription());
    }

    @Test
    public void markAsUndone_newOrCompletedTask_restoresIncompleteStatus() {
        Task task = new Task("book");
        task.markAsUndone();
        assertFalse(task.isDone());
        task.markAsDone();
        task.markAsUndone();
        task.markAsUndone();
        assertFalse(task.isDone());
        assertEquals("[ ] book", task.toString());
    }
}
