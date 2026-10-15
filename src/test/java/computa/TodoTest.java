package computa;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TodoTest {
    @Test void newTodoIsIncomplete() {
        Todo todo = new Todo("read book");
        assertEquals("[]", todo.getStatusIcon());
        assertEquals("[T][] read book\n", todo.getTaskDescription());
    }

    @Test void statusCanBeToggled() {
        Todo todo = new Todo("read book");
        todo.changeStatusIcon();
        assertEquals("[X]", todo.getStatusIcon());
        todo.changeStatusIcon();
        assertEquals("[]", todo.getStatusIcon());
    }

    @Test void todoFileFormatStoresCompletionAndDescription() {
        assertEquals("T | 0 | read book", new Todo("read book").toFileFormat());
        assertEquals("T | 1 | read book", new Todo("read book", true).toFileFormat());
    }
}
