package duck.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests todo display, persistence, status, and identity behavior.
 */
public class TodoTest {

    @Test
    public void toString_statusChanges_displaysCurrentStatus() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());

        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());

        todo.markAsUndone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void formatForStorage_specialCharacters_escapesStorageDelimiters() {
        Todo todo = new Todo("read A | B \\ C");
        todo.markAsDone();

        assertEquals("T | 1 | read A \\| B \\\\ C", todo.formatForStorage());
    }

    @Test
    public void hasSameDetails_sameDescriptionIgnoringStatus_returnsTrue() {
        Todo completedTodo = new Todo("read book");
        completedTodo.markAsDone();

        assertTrue(completedTodo.hasSameDetails(new Todo("read book")));
    }

    @Test
    public void hasSameDetails_differentDescriptionOrType_returnsFalse() {
        Todo todo = new Todo("read book");

        assertFalse(todo.hasSameDetails(new Todo("read another book")));
        assertFalse(todo.hasSameDetails(
                new Deadline("read book", LocalDate.of(2026, 10, 15))));
        assertFalse(todo.hasSameDetails(null));
    }
}
