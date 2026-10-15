package probe.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TodoTest {

    @Test
    void newTodoHasCorrectDescription() {
        Todo todo = new Todo("Read a book");

        assertEquals("Read a book", todo.getDescription());
    }

    @Test
    void newTodoIsNotDone() {
        Todo todo = new Todo("Read a book");

        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    void markAsDoneChangesStatus() {
        Todo todo = new Todo("Read a book");

        todo.markAsDone();

        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    void markAsUndoneChangesStatusBack() {
        Todo todo = new Todo("Read a book");
        todo.markAsDone();

        todo.markAsUndone();

        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    void toStringReturnsExpectedText() {
        Todo todo = new Todo("Read a book");

        assertEquals("[T][ ] Read a book", todo.toString());
    }

    @Test
    void toFileFormatReturnsExpectedText() {
        Todo todo = new Todo("Read a book");

        assertEquals("T | 0 | Read a book", todo.toFileFormat());
    }
}
