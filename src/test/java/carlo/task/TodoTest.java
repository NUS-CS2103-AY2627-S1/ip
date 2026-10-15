package carlo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Todo}.
 */
class TodoTest {

    @Test
    void toString_notDone_showsTTypeAndSpaceStatusIcon() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void toString_done_showsTTypeAndXStatusIcon() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    void toFileFormat_notDone_usesTTypeAndZeroFlag() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    void toFileFormat_done_usesTTypeAndOneFlag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    @Test
    void getDescription_returnsDescriptionGivenAtConstruction() {
        Todo todo = new Todo("read book");
        assertEquals("read book", todo.getDescription());
    }
}
