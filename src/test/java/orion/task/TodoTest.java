package orion.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TodoTest {
    @Test
    public void toFileFormat_uncompletedTodo_success() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    public void toFileFormat_completedTodo_success() {
        Todo todo = new Todo("read book", true);
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }
}
