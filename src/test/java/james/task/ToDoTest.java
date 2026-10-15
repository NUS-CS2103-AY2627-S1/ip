package james.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ToDo}.
 */
public class ToDoTest {

    /**
     * Tests that the constructor initializes the description and sets completion status to false.
     */
    @Test
    public void constructor_validDescription_setsDescription() {
        ToDo todo = new ToDo("buy groceries");
        assertEquals("buy groceries", todo.getDescription());
        assertFalse(todo.isDone());
    }

    /**
     * Tests that toFileString returns the standard serialized string format for an unmarked todo.
     */
    @Test
    public void toFileString_unmarkedTodo_returnsFormattedString() {
        ToDo todo = new ToDo("buy groceries");
        assertEquals("T | 0 | buy groceries", todo.toFileString());
    }

    /**
     * Tests that toFileString returns the standard serialized string format for a marked todo.
     */
    @Test
    public void toFileString_markedTodo_returnsFormattedString() {
        ToDo todo = new ToDo("buy groceries");
        todo.markDone();

        assertTrue(todo.isDone());
        assertEquals("T | 1 | buy groceries", todo.toFileString());
    }

    /**
     * Tests that toString returns the formatted display string for an unmarked todo.
     */
    @Test
    public void toString_unmarkedTodo_returnsDisplayString() {
        ToDo todo = new ToDo("buy groceries");
        assertEquals("[T][ ] buy groceries", todo.toString());
    }

    /**
     * Tests that toString returns the formatted display string for a marked todo.
     */
    @Test
    public void toString_markedTodo_returnsDisplayString() {
        ToDo todo = new ToDo("buy groceries");
        todo.markDone();

        assertEquals("[T][X] buy groceries", todo.toString());
    }
}
