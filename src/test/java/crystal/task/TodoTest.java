package crystal.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests todo-specific display and storage representations.
 */
public class TodoTest {
    /** Verifies the display and storage forms of an incomplete todo. */
    @Test
    public void representations_incompleteTodo_includeTypeAndStatus() {
        Todo todo = new Todo("read book");

        assertEquals("T | 0 | read book", todo.toDataString());
        assertEquals("[T][ ] read book", todo.toString());
    }

    /** Verifies the display and storage forms of a completed todo. */
    @Test
    public void representations_completedTodo_includeCompletedStatus() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("T | 1 | read book", todo.toDataString());
        assertEquals("[T][X] read book", todo.toString());
    }
}
