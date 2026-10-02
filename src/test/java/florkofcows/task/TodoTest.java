package florkofcows.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TodoTest {
    @Test
    public void toString_notDone_showsTypeTagAndEmptyBracket() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_done_showsTypeTagAndXBracket() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toSaveFormat_notDone_correctFormat() {
        assertEquals("T | 0 | read book", new Todo("read book").toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_correctFormat() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toSaveFormat());
    }

    @Test
    public void constructor_blankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Todo("   "));
    }
}
