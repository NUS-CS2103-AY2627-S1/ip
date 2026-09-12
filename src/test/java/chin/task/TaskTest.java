package chin.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void toString_unmarkedTodo_showsEmptyBox() {
        Todo t = new Todo("borrow book");
        assertEquals("[T][ ] borrow book", t.toString());
    }

    @Test
    public void toString_markedTodo_showsX() {
        Todo t = new Todo("borrow book");
        t.mark();
        assertEquals("[T][X] borrow book", t.toString());
    }

    @Test
    public void unmark_afterMark_showsEmptyBox() {
        Todo t = new Todo("borrow book");
        t.mark();
        t.unmark();
        assertEquals("[T][ ] borrow book", t.toString());
    }

    @Test
    public void serialize_todoRoundTrip_preservesState() {
        Todo t = new Todo("read");
        t.mark();
        assertEquals("T | 1 | read", t.serialize());
    }
}
