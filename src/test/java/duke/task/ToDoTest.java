package duke.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ToDoTest {

    @Test
    public void toDataString_notDone_usesTaskFormat() {
        ToDo todo = new ToDo("read book");
        assertEquals("T | 0 | read book", todo.toDataString());
    }

    @Test
    public void toDataString_done_usesTaskFormat() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toDataString());
    }

    @Test
    public void toString_notDone_includesTypeAndStatus() {
        ToDo todo = new ToDo("read book");
        assertEquals("[T] [ ] read book", todo.toString());
    }

    @Test
    public void toString_done_includesTypeAndStatus() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        assertEquals("[T] [X] read book", todo.toString());
    }

    @Test
    public void toString_emptyDescription_keepsExpectedSpacing() {
        ToDo todo = new ToDo("");
        assertEquals("[T] [ ] ", todo.toString());
    }
}

