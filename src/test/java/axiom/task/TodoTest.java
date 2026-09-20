package axiom.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TodoTest {

    @Test
    void toString_notDone_includesTypePrefixAndEmptyStatus() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void toString_done_includesTypePrefixAndDoneStatus() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    void getDescription_returnsDescription() {
        Todo todo = new Todo("return book");
        assertEquals("return book", todo.getDescription());
    }

    @Test
    void markAsDone_newTodo_marksTaskDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    void markAsNotDone_doneTodo_marksTaskNotDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertFalse(todo.isDone());
        assertEquals(" ", todo.getStatusIcon());
    }
}
