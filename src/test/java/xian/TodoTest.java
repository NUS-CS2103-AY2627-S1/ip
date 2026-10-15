package xian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toString_notDone_correctFormat() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_marked_correctFormat() {
        Todo todo = new Todo("read book");
        todo.mark();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void mark_setsIsDoneTrue() {
        Todo todo = new Todo("read book");
        todo.mark();
        assertTrue(todo.toString().contains("[X]"));
    }

    @Test
    public void unmark_afterMark_setsIsDoneFalse() {
        Todo todo = new Todo("read book");
        todo.mark();
        todo.unmark();
        assertFalse(todo.toString().contains("[X]"));
    }

    @Test
    public void getDescription_returnsCorrectDescription() {
        Todo todo = new Todo("read book");
        assertEquals("read book", todo.getDescription());
    }

    @Test
    public void getStatusCode_notDone_returnsZero() {
        Todo todo = new Todo("read book");
        assertEquals("0", todo.getStatusCode());
    }

    @Test
    public void getStatusCode_done_returnsOne() {
        Todo todo = new Todo("read book");
        todo.mark();
        assertEquals("1", todo.getStatusCode());
    }
}
