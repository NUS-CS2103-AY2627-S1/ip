package walnut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    void parseTask_todo_createsTodo() {
        Task task = Parser.parseTask("T | 0 | buy milk");

        assertTrue(task instanceof ToDo);
        assertEquals("buy milk", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    void parseTask_completedTodo_createsCompletedTodo() {
        Task task = Parser.parseTask("T | 1 | buy milk");

        assertTrue(task instanceof ToDo);
        assertTrue(task.isDone());
    }

    @Test
    void parseTask_unknownType_returnsNull() {
        Task task = Parser.parseTask("X | 0 | unknown task");

        assertNull(task);
    }

    @Test
    void parseTask_invalidCompletionStatus_returnsNull() {
        Task task = Parser.parseTask("T | 2 | invalid status");

        assertNull(task);
    }

    @Test
    void parseTask_missingDescription_returnsNull() {
        Task task = Parser.parseTask("T | 0 | ");

        assertNull(task);
    }
}
