package oz.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ToDo}.
 */
public class ToDoTest {

    @Test
    public void toFileFormat_unmarkedAndMarked_correctFormat() {
        ToDo unmarkedTodo = new ToDo("borrow book");
        assertEquals("T | 0 | borrow book", unmarkedTodo.toFileFormat());

        ToDo markedTodo = new ToDo("borrow book");
        markedTodo.markAsDone();
        assertEquals("T | 1 | borrow book", markedTodo.toFileFormat());
    }

    @Test
    public void toString_unmarkedAndMarked_correctStringRepresentation() {
        ToDo unmarkedTodo = new ToDo("read newspaper");
        assertEquals("[T][ ] read newspaper", unmarkedTodo.toString());

        ToDo markedTodo = new ToDo("read newspaper");
        markedTodo.markAsDone();
        assertEquals("[T][X] read newspaper", markedTodo.toString());
    }
}
