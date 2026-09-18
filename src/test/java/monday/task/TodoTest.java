package monday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for Todo.
 */
class TodoTest {
    @Test
    void constructor_withDescription_createsIncompleteTodo() {
        Todo todo = new Todo("buy groceries");

        assertEquals("buy groceries", todo.getDescription());
        assertFalse(todo.isDone());
        assertEquals("[T][ ] buy groceries", todo.toString());
    }

    @Test
    void constructor_withCompletionStatus_createsTodoWithGivenStatus() {
        Todo todo = new Todo("buy groceries", true);

        assertTrue(todo.isDone());
        assertEquals("[T][X] buy groceries", todo.toString());
    }

    @Test
    void constructor_withNotes_storesAndDisplaysNotes() {
        Todo todo = new Todo("buy groceries", "Use the voucher");

        assertEquals("Use the voucher", todo.getNotes());
        assertEquals("[T][ ] buy groceries Use the voucher", todo.toString());
    }

    @Test
    void markAsDone_marksTodo() {
        Todo todo = new Todo("buy groceries");

        todo.markAsDone();

        assertTrue(todo.isDone());
        assertEquals("[T][X] buy groceries", todo.toString());
    }

    @Test
    void markAsNotDone_unmarksCompletedTodo() {
        Todo todo = new Todo("buy groceries", true);

        todo.markAsNotDone();

        assertFalse(todo.isDone());
        assertEquals("[T][ ] buy groceries", todo.toString());
    }
}
