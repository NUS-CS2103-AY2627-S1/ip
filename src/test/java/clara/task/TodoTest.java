package clara.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

// NOTE: AI-assisted test suite implementation. See CITATIONS.md [C-010].

/**
 * Unit tests for {@link Todo}.
 */
public class TodoTest {

    @Test
    // NOTE: AI-assisted task priority feature. See CITATIONS.md [C-018].
    public void toString_uncompletedTodo_correctFormat() {
        Todo todo = new Todo("buy groceries");
        assertFalse(todo.isDone());
        assertEquals("buy groceries", todo.getTaskName());
        assertEquals("[T][ ] Medium buy groceries", todo.toString());
    }

    @Test
    public void toString_completedTodo_correctFormat() {
        Todo todo = new Todo("buy groceries");
        todo.setDone(true);
        assertTrue(todo.isDone());
        assertEquals("[T][X]", todo.toString().substring(0, 6));
        assertEquals("[T][X] Medium buy groceries", todo.toString());
    }

    @Test
    public void setTaskName_validName_nameUpdated() {
        Todo todo = new Todo("buy groceries");
        todo.setTaskName("buy fruits");
        assertEquals("buy fruits", todo.getTaskName());
    }

    @Test
    // NOTE: AI-assisted assertion test. See CITATIONS.md [C-015].
    public void constructor_blankName_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Todo(" "));
    }
}
