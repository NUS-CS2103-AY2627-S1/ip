package lebron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests for {@link Todo}'s display string and its data-file encoding. */
public class TodoTest {

    @Test
    public void toString_notDone_showsEmptyStatusBox() {
        assertEquals("[ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_done_showsCrossInStatusBox() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[X] read book", todo.toString());
    }

    @Test
    public void toFileFormat_notDone_usesZeroFlag() {
        assertEquals("T | 0 | read book", new Todo("read book").toFileFormat());
    }

    @Test
    public void toFileFormat_done_usesOneFlag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }
}
