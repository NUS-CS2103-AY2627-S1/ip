package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void getTypeName_todo_returnsTodo() {
        assertEquals("Todo", new Todo("read book").getTypeName());
    }

    @Test
    public void getScheduleSummary_todo_isEmptyBecauseItHasNoDate() {
        assertEquals("", new Todo("read book").getScheduleSummary());
    }

    @Test
    public void newTodo_startsNotDone() {
        assertFalse(new Todo("read book").isDone());
        assertEquals(" ", new Todo("read book").getStatusIcon());
    }

    @Test
    public void markAsDone_thenUnmark_returnsToNotDone() {
        Todo todo = new Todo("read book");

        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("X", todo.getStatusIcon());

        todo.markAsNotDone();
        assertFalse(todo.isDone());
    }

    @Test
    public void toString_showsTypeMarkerAndStatus() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toFileFormat_marksDoneStateAsOneOrZero() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toFileFormat());

        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }
}
