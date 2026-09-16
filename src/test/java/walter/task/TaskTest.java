package walter.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests completion state and base task formatting.
 */
public class TaskTest {
    @Test
    public void constructor_newTask_initiallyNotDone() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void markAsDone_notDoneTask_doneStateAndFormattingUpdated() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void markAsNotDone_doneTask_notDoneStateAndFormattingRestored() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void toString_todo_typeAndStatusFormattingIncluded() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }
}
