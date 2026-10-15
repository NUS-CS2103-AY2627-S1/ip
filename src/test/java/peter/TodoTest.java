package peter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Todo}.
 */
public class TodoTest {
    @Test
    public void toString_notDone_showsTypeAndEmptyStatusIcon() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_done_showsTypeAndXStatusIcon() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toFileFormat_usesTodoTypeCode() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    public void equals_sameDescription_returnsTrue() {
        assertEquals(new Todo("read book"), new Todo("read book"));
    }
}
