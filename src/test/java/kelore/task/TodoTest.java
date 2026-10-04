package kelore.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests display and storage behavior of {@link Todo}. */
public class TodoTest {
    @Test
    public void displayAndStorage_markedTodo_includeTypeAndStatus() {
        Todo todo = new Todo("read a book");

        assertEquals("[T][ ] read a book", todo.toString());
        assertEquals("T | 0 | read a book", todo.toStorageString());

        todo.markAsDone();
        assertEquals("[T][X] read a book", todo.toString());
        assertEquals("T | 1 | read a book", todo.toStorageString());
    }
}
