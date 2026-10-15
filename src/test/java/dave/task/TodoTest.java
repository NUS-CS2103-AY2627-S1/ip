package dave.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Todo} class.
 */
public class TodoTest {

    @Test
    public void toFileFormat_unmarkedTodo_returnsCorrectFormat() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    public void toFileFormat_markedTodo_returnsCorrectFormat() {
        Todo todo = new Todo("read book");
        todo.setDone(true);
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    @Test
    public void toString_unmarkedTodo_returnsCorrectRepresentation() {
        Todo todo = new Todo("join meeting");
        assertEquals("[T][ ] join meeting", todo.toString());
    }

    @Test
    public void toString_markedTodo_returnsCorrectRepresentation() {
        Todo todo = new Todo("join meeting");
        todo.setDone(true);
        assertEquals("[T][X] join meeting", todo.toString());
    }
}
