package vector.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toString_validTodo_correctFormat() {
        Todo t = new Todo("read book");
        assertEquals("[T][ ] read book", t.toString());
        t.markAsDone();
        assertEquals("[T][X] read book", t.toString());
    }

    @Test
    public void toFileFormat_validTodo_correctFormat() {
        Todo t = new Todo("read book");
        assertEquals("T | 0 | read book", t.toFileFormat());
        t.markAsDone();
        assertEquals("T | 1 | read book", t.toFileFormat());
    }

    @Test
    public void equals_sameTodos_returnsTrue() {
        Todo t1 = new Todo("read book");
        Todo t2 = new Todo("read book");
        assertTrue(t1.equals(t2));
        assertTrue(t1.equals(t1));
    }

    @Test
    public void equals_differentTodos_returnsFalse() {
        Todo t1 = new Todo("read book");
        Todo t2 = new Todo("borrow book");
        assertFalse(t1.equals(t2));
        assertFalse(t1.equals(null));
    }

    @Test
    public void occursOn_alwaysReturnsFalse() {
        Todo t = new Todo("read book");
        assertFalse(t.occursOn(LocalDate.now()));
    }
}
