package mouse.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Task} done status and {@link ToDo} display.
 */
public class ToDoTest {
    @Test
    public void constructor_validDescription_storesDescription() {
        ToDo todo = new ToDo("read book");
        assertEquals("read book", todo.getDescription());
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void markAsDone_incompleteTask_statusIconIsX() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void markAsNotDone_completedTask_statusIconIsBlank() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void toString_incompleteTodo_includesTypeAndStatus() {
        ToDo todo = new ToDo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_completedTodo_showsDoneStatus() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }
}
