package marcus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the display and persistence behavior of {@link Todo}.
 */
class TodoTest {
    /** Verifies that a new to-do displays its type and incomplete status. */
    @Test
    void toString_newTodo_showsTodoTypeAndIncompleteStatus() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    /** Verifies that marking a to-do changes its displayed status to completed. */
    @Test
    void markAsDone_incompleteTodo_showsCompletedStatus() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    /** Verifies that unmarking a completed to-do restores its incomplete status. */
    @Test
    void unmarkAsDone_completedTodo_showsIncompleteStatus() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        todo.unmarkAsDone();

        assertEquals("[T][ ] read book", todo.toString());
    }

    /** Verifies that a to-do does not occur on a dated schedule. */
    @Test
    void occursOn_anyDate_returnsFalse() {
        Todo todo = new Todo("read book");

        assertFalse(todo.occursOn(LocalDate.parse("2026-08-27")));
    }

    /** Verifies that a new to-do is persisted with an incomplete status. */
    @Test
    void toFileString_newTodo_savesIncompleteStatus() {
        Todo todo = new Todo("read book");

        assertEquals("T | 0 | read book", todo.toFileString());
    }

    /** Verifies that a completed to-do is persisted with a completed status. */
    @Test
    void toFileString_completedTodo_savesCompletedStatus() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("T | 1 | read book", todo.toFileString());
    }
}
