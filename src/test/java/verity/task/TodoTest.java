package verity.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TodoTest {

    @Test
    void toString_notDone_prependsTypeTag() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void toString_done_showsXInsideTypeTag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    void toSaveFormat_notDone_prependsTTypeField() {
        Todo todo = new Todo("read book");

        assertEquals("T | 0 | read book", todo.toSaveFormat());
    }

    @Test
    void toSaveFormat_done_savesDoneFlagAsOne() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("T | 1 | read book", todo.toSaveFormat());
    }
}
