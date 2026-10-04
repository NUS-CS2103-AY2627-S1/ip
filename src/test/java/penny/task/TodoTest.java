package penny.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for {@link Todo}.
 * Verifies todo task formatting, string representation, and storage serialization.
 */
public class TodoTest {

    private Todo todo;

    @BeforeEach
    public void setUp() {
        todo = new Todo("read book");
    }

    @Test
    public void constructor_validDescription_createsTodo() {
        assertEquals("read book", todo.getDescription());
    }

    @Test
    public void toString_undoneTodo_returnsFormattedStringWithT() {
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_doneTodo_returnsFormattedStringWithX() {
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toFileFormat_undoneTodo_returnsPipeSeparatedString() {
        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    public void toFileFormat_doneTodo_returnsPipeSeparatedString() {
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }
}
