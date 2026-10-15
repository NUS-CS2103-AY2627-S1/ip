package erina.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the behaviour every kind of task shares, defined in {@link Task}.
 *
 * <p>Task is abstract, so it is exercised through {@link Todo}, the kind that
 * adds nothing of its own.
 */
public class TaskTest {
    @Test
    public void newTask_isNotDone() {
        Todo todo = new Todo("read book");
        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void markAsDone_thenMarkAsNotDone_updatesStatusEachTime() {
        Todo todo = new Todo("read book");

        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("X", todo.getStatusIcon());

        todo.markAsNotDone();
        assertFalse(todo.isDone());
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void toSaveString_todo_hasTypeFlagAndDescription() {
        assertEquals("T | 0 | read book", new Todo("read book").toSaveString());
    }

    @Test
    public void isSameTask_oneDoneOneNot_isTrue() {
        // Being done is progress on a task, not part of what the task is.
        Todo done = new Todo("read book");
        done.markAsDone();
        assertTrue(done.isSameTask(new Todo("read book")));
    }

    @Test
    public void isSameTask_differentKindsSameDescription_isFalse() {
        Todo todo = new Todo("meeting");
        Event event = new Event("meeting", "Mon", "Tue");
        assertFalse(todo.isSameTask(event));
    }
}
