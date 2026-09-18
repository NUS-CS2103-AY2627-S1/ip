package eden.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests display, state, and storage behavior shared by non-deadline task types.
 */
public class TaskTest {
    /**
     * Verifies todo state changes and escaping in its stored representation.
     */
    @Test
    public void todo_markAndSerialize_preservesDescriptionAndEscapesData() {
        Todo todo = new Todo("read | book \\ notes");

        assertFalse(todo.isMarked());
        assertEquals("[T][ ] read | book \\ notes", todo.toString());
        assertEquals("T | 0 | read \\| book \\\\ notes", todo.toDataString());

        todo.mark();

        assertTrue(todo.isMarked());
        assertEquals("[T][X] read | book \\ notes", todo.toString());
        assertEquals("T | 1 | read \\| book \\\\ notes", todo.toDataString());

        todo.unmark();
        assertFalse(todo.isMarked());
    }

    /**
     * Verifies that event display and storage include and escape both time fields.
     */
    @Test
    public void event_displayAndSerialize_includesTimeRange() {
        Event event = new Event("team sync", "2pm | office", "3pm \\ online");

        assertEquals("[E][ ] team sync (from: 2pm | office to: 3pm \\ online)",
                event.toString());
        assertEquals("E | 0 | team sync | 2pm \\| office | 3pm \\\\ online",
                event.toDataString());
    }
}
