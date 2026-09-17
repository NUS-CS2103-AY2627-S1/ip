package lebron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {
    @Test
    public void equals_sameDescription_returnsTrue() {
        assertEquals(new Todo("read book"), new Todo("read book"));
    }

    @Test
    public void equals_differentDescription_returnsFalse() {
        assertNotEquals(new Todo("read book"), new Todo("write essay"));
    }

    @Test
    public void toString_notDone_correctFormat() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_done_correctFormat() {
        Todo todo = new Todo("read book");
        todo.setStatus(true);
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void reformat_notDone_correctSaveFormat() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.reformat());
    }

    @Test
    public void reformat_done_correctSaveFormat() {
        Todo todo = new Todo("read book");
        todo.setStatus(true);
        assertEquals("T | 1 | read book", todo.reformat());
    }
}
