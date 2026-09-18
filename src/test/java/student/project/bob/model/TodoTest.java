package student.project.bob.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the formatting behavior of todo tasks.
 */
public class TodoTest {
    /**
     * Verifies that a todo has the expected type prefix and incomplete status.
     */
    @Test
    public void constructor_todo_formatsDescriptionAndStatus() {
        Todo todo = new Todo("read book");

        assertEquals("read book", todo.getDescription());
        assertEquals("[T][ ] read book", todo.toString());
    }

    /**
     * Verifies that a completed todo includes both its type and completed status.
     */
    @Test
    public void toString_completedTodo_includesCompletedStatus() {
        Todo todo = new Todo("read book");

        todo.setDone();

        assertEquals("[T][X] read book", todo.toString());
    }
}
