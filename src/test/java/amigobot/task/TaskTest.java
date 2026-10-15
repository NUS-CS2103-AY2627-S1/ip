package amigobot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the base Task class behavior.
 */
public class TaskTest {

    @Test
    public void constructor_setsDescriptionAndNotDone() {
        Task task = new Todo("read book");
        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    public void getStatusIcon_notDone_returnsSpace() {
        Task task = new Todo("task");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_done_returnsX() {
        Task task = new Todo("task");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsDone_setsIsDoneTrue() {
        Task task = new Todo("task");
        task.markAsDone();
        assertTrue(task.isDone());
    }

    @Test
    public void markAsNotDone_afterMarkDone_setsIsDoneFalse() {
        Task task = new Todo("task");
        task.markAsDone();
        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    public void toString_notDone_formatsCorrectly() {
        Task task = new Todo("read book");
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void toString_done_formatsCorrectly() {
        Task task = new Todo("read book");
        task.markAsDone();
        assertEquals("[T][X] read book", task.toString());
    }
}
