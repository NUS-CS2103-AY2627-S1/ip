package atom.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ToDoTest {

    @Test
    public void mark() {
        ToDo markedToDo = new ToDo("borrow book");
        markedToDo.mark();
        assertEquals("[T][X] borrow book", markedToDo.toString());
    }

    @Test
    public void unmark() {
        ToDo unmarkedToDo = new ToDo("borrow book");
        unmarkedToDo.mark();
        unmarkedToDo.unmark();
        assertEquals("[T][ ] borrow book", unmarkedToDo.toString());
    }

    @Test
    public void testStringConversion() {
        assertEquals("[T][ ] borrow book", new ToDo("borrow book").toString());
    }
}
