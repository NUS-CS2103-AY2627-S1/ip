package monday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for Task.
 */
class TaskTest {
    @Test
    void constructor_withDescription_createsIncompleteTask() {
        Task task = new Task("read chapter");

        assertEquals("read chapter", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read chapter", task.toString());
    }

    @Test
    void constructor_withDescriptionAndCompletionStatus_createsTaskWithGivenStatus() {
        Task completedTask = new Task("read chapter", true);
        Task incompleteTask = new Task("read chapter", false);

        assertTrue(completedTask.isDone());
        assertEquals("[X] read chapter", completedTask.toString());
        assertFalse(incompleteTask.isDone());
        assertEquals("[ ] read chapter", incompleteTask.toString());
    }

    @Test
    void constructor_withNotes_storesAndDisplaysNotes() {
        Task task = new Task("read chapter", "Focus on inheritance");

        assertEquals("Focus on inheritance", task.getNotes());
        assertEquals("[ ] read chapter Focus on inheritance", task.toString());
    }

    @Test
    void markAsDone_marksIncompleteTask() {
        Task task = new Task("read chapter");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read chapter", task.toString());
    }

    @Test
    void markAsNotDone_unmarksCompletedTask() {
        Task task = new Task("read chapter", true);

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read chapter", task.toString());
    }
}
