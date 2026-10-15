package gunna.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test class for testing the Todo class.
 * Tests cover creation, string representations, and file format.
 */
public class TodoTest {

    private Todo todo;

    @BeforeEach
    public void setUp() {
        todo = new Todo("Buy groceries");
    }

    @Test
    public void constructor_newTodo_isNotDone() {
        assertFalse(todo.isDone(), "New todo should not be marked as done");
    }

    @Test
    public void constructor_newTodo_hasCorrectDescription() {
        assertEquals("Buy groceries", todo.getDescription(),
                "Todo description should match the constructor argument");
    }

    @Test
    public void toString_notDoneTodo_correctFormat() {
        assertEquals("[T][ ] Buy groceries", todo.toString(),
                "String representation should show [T] prefix for Todo");
    }

    @Test
    public void toString_doneTodo_correctFormat() {
        todo.markAsDone();
        assertEquals("[T][X] Buy groceries", todo.toString(),
                "String representation should show [T] prefix and [X] for done todo");
    }

    @Test
    public void toFileFormat_notDoneTodo_correctFormat() {
        assertEquals("T | 0 | Buy groceries", todo.toFileFormat(),
                "File format should use 'T' as task type indicator");
    }

    @Test
    public void toFileFormat_doneTodo_correctFormat() {
        todo.markAsDone();
        assertEquals("T | 1 | Buy groceries", todo.toFileFormat(),
                "File format should have 1 as status indicator for done todo");
    }

    @Test
    public void toFileFormat_emptyDescription_correctFormat() {
        Todo emptyTodo = new Todo("");
        assertEquals("T | 0 | ", emptyTodo.toFileFormat(),
                "File format should handle empty description");
    }

    @Test
    public void toString_emptyDescription_correctFormat() {
        Todo emptyTodo = new Todo("");
        assertEquals("[T][ ] ", emptyTodo.toString(),
                "toString should handle empty description");
    }

    @Test
    public void constructor_descriptionWithPipeCharacter_handlesCorrectly() {
        Todo specialTodo = new Todo("Task with | pipe character");
        assertEquals("Task with | pipe character", specialTodo.getDescription(),
                "Todo should preserve pipe character in description");
    }

    @Test
    public void toFileFormat_afterMarkAndUnmark_correctFormat() {
        todo.markAsDone();
        todo.markAsNotDone();
        assertEquals("T | 0 | Buy groceries", todo.toFileFormat(),
                "File format should reflect current done status after mark/unmark");
    }

    @Test
    public void constructor_longDescription_handlesCorrectly() {
        String longDesc = "This is a very long todo description that contains many words "
                + "and should be handled correctly by the Todo class without any issues";
        Todo longTodo = new Todo(longDesc);
        assertEquals(longDesc, longTodo.getDescription(),
                "Todo should handle long descriptions");
        assertTrue(longTodo.toString().contains(longDesc),
                "toString should include the full long description");
    }
}
