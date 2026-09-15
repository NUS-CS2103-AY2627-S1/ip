package tianyi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests todo persistence and display formatting.
 */
public class ToDoTest {
    @Test
    public void formatting_incompleteTodo_includesTypeAndStatus() {
        ToDo task = new ToDo("read book");

        assertEquals("T | 0 | read book", task.getData());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void formatting_completedTodo_includesCompletedStatus() {
        ToDo task = new ToDo("read book");
        task.markAsDone();

        assertEquals("T | 1 | read book", task.getData());
        assertEquals("[T][X] read book", task.toString());
    }
}
