package chattingheads.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toString_incompleteTodo_returnsFormattedString() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_completedTodo_returnsFormattedString() {
        Todo todo = new Todo("read book", true);
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toCsv_incompleteTodo_returnsCsvString() {
        Todo todo = new Todo("read book");
        assertEquals("T,read book,false", todo.toCsv());
    }

    @Test
    public void toCsv_completedTodo_returnsCsvString() {
        Todo todo = new Todo("read book", true);
        assertEquals("T,read book,true", todo.toCsv());
    }
}
