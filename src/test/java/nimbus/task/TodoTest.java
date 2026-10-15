package nimbus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toString_unmarkedTodo_showsCorrectFormat() {
        Todo todo = new Todo("read book");
        assertEquals("[T] [ ] read book", todo.toString());
    }

    @Test
    public void toString_markedTodo_showsXInBrackets() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T] [X] read book", todo.toString());
    }

    @Test
    public void toString_unmarkedAfterUnmark_showsEmptyBrackets() {
        // Covers the unmark path: mark then unmark should return to the original
        // format.
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.unmarkAsDone();
        assertEquals("[T] [ ] read book", todo.toString());
    }

    @Test
    public void toFileString_unmarkedTodo_producesCorrectSaveFormat() {
        Todo todo = new Todo("read book");
        assertEquals("T|0|read book", todo.toFileString());
    }

    @Test
    public void toFileString_markedTodo_producesCorrectSaveFormat() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T|1|read book", todo.toFileString());
    }
}
