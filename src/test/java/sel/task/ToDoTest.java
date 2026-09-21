package sel.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ToDoTest {

    @Test
    public void toString_notDone_showsEmptyStatusBox() {
        ToDo todo = new ToDo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_marked_showsXInStatusBox() {
        ToDo todo = new ToDo("read book");
        todo.mark();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toString_markedThenUnmarked_showsEmptyStatusBoxAgain() {
        ToDo todo = new ToDo("read book");
        todo.mark();
        todo.unmark();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_preservesDescriptionExactly() {
        ToDo todo = new ToDo("join sports club");
        assertEquals("[T][ ] join sports club", todo.toString());
    }
}
