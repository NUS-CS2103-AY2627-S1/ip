package ruby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for the {@code Todo} task class.
 */
class TodoTest {

    @Test
    void newTodo_isNotDone() {
        Todo todo = new Todo("read book");
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    void newTodo_toDataString_hasDoneFlagZero() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toDataString());
    }

    @Test
    void toString_includesTodoIconAndDescription() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void markAsDone_updatesStatusIconToX() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    void markAsDone_updatesToStringAndDataString() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T | 1 | read book", todo.toDataString());
    }

    @Test
    void markAsNotDone_afterBeingDone_revertsStatus() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertEquals(" ", todo.getStatusIcon());
        assertEquals("[T][ ] read book", todo.toString());
    }
}
