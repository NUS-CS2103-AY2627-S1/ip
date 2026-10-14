package bingus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * Tests the behaviour of {@link Todo} tasks.
 */
class TodoTest {
    /**
     * Verifies that a newly-created todo is initially incomplete.
     */
    @Test
    void getStatusIcon_newTodo_returnsIncompleteIcon() {
        Todo todo = new Todo("read the JUnit guide");

        assertEquals("○", todo.getStatusIcon());
    }

    @Test
    void getStatusIcon_todoMarked_returnsMarkedIcon() {
        Todo todo = new Todo("read the JUnit guide");
        todo.mark();

        assertEquals("✓", todo.getStatusIcon());
    }

    /**
     * Verifies that a todo does not match any date because it has no date-related details.
     */
    @Test
    void matchesDate_todo_returnsFalse() {
        Todo todo = new Todo("read the JUnit guide");

        assertFalse(todo.matchesDate(java.time.LocalDate.of(2026, 9, 8)));
    }

    /**
     * Verifies that marking and unmarking a todo changes its display string and completion status.
     */
    @Test
    void markThenUnmark_todo_updatesStatusAndDisplayString() {
        Todo todo = new Todo("read the JUnit guide");

        todo.mark();
        assertEquals("[T][✓] read the JUnit guide", todo.getDisplayString());

        todo.unmark();
        assertEquals("[T][○] read the JUnit guide", todo.getDisplayString());
    }
}
