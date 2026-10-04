package happy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Todo task class.
 */
public class TodoTest {

    @Test
    public void toString_uncompletedTask_correctStringFormat() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_completedTask_correctStringFormat() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toFileFormat_uncompletedTask_correctStorageFormat() {
        Todo todo = new Todo("buy groceries");
        assertEquals("T | 0 | buy groceries", todo.toFileFormat());
    }

    @Test
    public void toFileFormat_withPriority_correctStorageFormat() {
        Todo todo = new Todo("buy groceries");
        todo.setPriority(Priority.HIGH);
        assertEquals("T | 0 | HIGH | buy groceries", todo.toFileFormat());
    }

    @Test
    public void equals_sameDescription_returnsTrue() {
        Todo todo1 = new Todo("read book");
        Todo todo2 = new Todo("READ BOOK");
        assertTrue(todo1.equals(todo2));
    }

    @Test
    public void equals_differentDescription_returnsFalse() {
        Todo todo1 = new Todo("read book");
        Todo todo2 = new Todo("write code");
        assertFalse(todo1.equals(todo2));
    }
}
