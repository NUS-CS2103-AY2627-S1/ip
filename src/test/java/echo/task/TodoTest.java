package echo.task; // same package as the class being tested

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TodoTest {
    @Test
    public void toString_newTodo_showsNotDoneTag() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_markedDone_showsDoneTag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toFileFormat_newTodo_returnsPipeDelimitedLine() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    public void occursOn_anyDate_returnsFalse() {
        Todo todo = new Todo("read book");
        assertFalse(todo.occursOn(LocalDate.of(2019, 10, 15)));
    }
}
