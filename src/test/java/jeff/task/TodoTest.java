package jeff.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TodoTest {
    @Test
    public void constructor_blankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Todo(""));
    }

    @Test
    public void toString_notDone_showsTypeAndBlankStatus() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_done_showsXStatus() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toSaveFormat_notDone_encodesTypeAndFields() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_encodesDoneFlag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toSaveFormat());
    }
}
