package jasper.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jasper.JasperException;
import jasper.task.Todo;

class TodoSerializerTest {
    @Test
    public void serialize_uncompletedTodo_correctFormat() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", TodoSerializer.serialize(todo));
    }

    @Test
    public void serialize_completedTodo_correctFormat() {
        Todo todo = new Todo("read book");
        todo.markDone();
        assertEquals("T | 1 | read book", TodoSerializer.serialize(todo));
    }

    @Test
    void deserialize_validUncompletedString_todoReturned() throws JasperException {
        Todo todo = TodoSerializer.deserialize("T | 0 | read book");
        assertEquals("read book", todo.getDescription());
        assertFalse(todo.isDone());
    }

    @Test
    void deserialize_validCompletedString_markedTodoReturned() throws JasperException {
        Todo todo = TodoSerializer.deserialize("T | 1 | read book");
        assertEquals("read book", todo.getDescription());
        assertTrue(todo.isDone());
    }

    @Test
    void deserialize_invalidFormat_exceptionThrown() {
        JasperException ex = assertThrows(JasperException.class, () -> TodoSerializer.deserialize("T | 0"));
        assertEquals("Error reading savefile!", ex.getMessage());
    }
}
