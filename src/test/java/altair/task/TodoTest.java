package altair.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Todo}.
 *
 * <p>{@code Todo} adds no behavior of its own beyond a constructor, so these
 * tests exist to lock in the behavior it <em>inherits</em>: a plain task with
 * no date must display and serialize with the {@code T} type marker.</p>
 */
public class TodoTest {

    @Test
    public void toString_newTodo_showsTypeMarkerAndBlankStatus() {
        Todo todo = new Todo("borrow book");
        assertEquals("[T][ ] borrow book", todo.toString());
    }

    @Test
    public void toString_doneTodo_showsXStatus() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("[T][X] borrow book", todo.toString());
    }

    @Test
    public void toFileString_newTodo_serializesWithTypeTAndZeroStatus() {
        Todo todo = new Todo("borrow book");
        assertEquals("T | 0 | borrow book", todo.toFileString());
    }

    @Test
    public void toFileString_doneTodo_serializesWithStatusOne() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("T | 1 | borrow book", todo.toFileString());
    }
}
