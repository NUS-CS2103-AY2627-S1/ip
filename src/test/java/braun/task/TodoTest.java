package braun.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Todo} model behaviors.
 */
public class TodoTest {

    @Test
    public void constructor_validDescription_success() {
        Todo todo = new Todo("investigate ghost room");
        assertEquals("investigate ghost room", todo.getDescription());
        assertEquals("[T][ ] investigate ghost room", todo.toString());
        assertEquals("T | 0 | investigate ghost room", todo.toFileFormat());
    }

    @Test
    public void markAsDone_stateToggled_updatesStatus() {
        Todo todo = new Todo("investigate ghost room");
        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("[T][X] investigate ghost room", todo.toString());
        assertEquals("T | 1 | investigate ghost room", todo.toFileFormat());

        todo.markAsUndone();
        assertFalse(todo.isDone());
    }

    @Test
    public void isOnDate_anyDate_returnsFalse() {
        Todo todo = new Todo("investigate ghost room");
        assertFalse(todo.isOnDate(LocalDate.of(2026, 8, 30)));
    }

    @Test
    public void equals_identicalDescription_returnsTrue() {
        Todo first = new Todo("investigate ghost room");
        Todo second = new Todo("INVESTIGATE GHOST ROOM");
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals_differentDescription_returnsFalse() {
        Todo first = new Todo("investigate ghost room");
        Todo second = new Todo("pat pink rabbit");
        assertFalse(first.equals(second));
    }
}
