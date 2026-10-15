package rocky.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the behavior specific to {@link Todo} tasks. */
class TodoTest {
    /** Verifies that a new to-do is incomplete in display and storage formats. */
    @Test
    void constructor_newTodo_displaysAndStoresIncompleteStatus() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toStorageString());
        assertTrue(todo.getSortDate().isEmpty());
    }

    /** Verifies that marking and unmarking a to-do changes both representations. */
    @Test
    void markAndUnmark_newTodo_updatesDisplayAndStorageStatus() {
        Todo todo = new Todo("read book");

        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T | 1 | read book", todo.toStorageString());

        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toStorageString());
    }

    /** Verifies that keyword matching is case-insensitive and rejects null keywords. */
    @Test
    void matchesKeyword_caseInsensitiveAndNullKeyword_returnsExpectedResult() {
        Todo todo = new Todo("Read a Book");

        assertTrue(todo.matchesKeyword("BOOK"));
        assertFalse(todo.matchesKeyword("meeting"));
        assertFalse(todo.matchesKeyword(null));
    }
}
