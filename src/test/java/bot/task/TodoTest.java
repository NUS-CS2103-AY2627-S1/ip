package bot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TodoTest {

    @Test
    void toString_notDone_showsTypeIconAndSpaceIcon() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void toString_done_showsTypeIconAndXIcon() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    void toSaveFormat_notDone_usesZeroFlag() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toSaveFormat());
    }

    @Test
    void toSaveFormat_done_usesOneFlag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toSaveFormat());
    }
}
