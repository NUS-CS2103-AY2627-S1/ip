package larper.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {
    @Test
    public void toStringAndFileString_unmarkedTodo_expectedFormat() {
        Todo todo = new Todo("read book");

        assertEquals("read book", todo.getDescription());
        assertEquals("T", todo.getTypeIcon());
        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toFileString());
    }

    @Test
    public void toStringAndFileString_markedTodo_expectedFormat() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertEquals("X", todo.getStatusIcon());
        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T | 1 | read book", todo.toFileString());
    }
}
