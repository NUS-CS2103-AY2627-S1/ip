package grower.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Verifies serialization of completed to-do tasks.
 */
public class ToDoTest {
    @Test
    public void toFileString_completeTodo_returnsCompleteTodoSerializedTodo() {
        ToDo todo = new ToDo("submit assignment");
        todo.mark();

        assertEquals("T | 1 | submit assignment", todo.toFileString());
    }
}
