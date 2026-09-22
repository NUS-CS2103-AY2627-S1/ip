package mono.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests to-do display and persistence representations.
 */
public class ToDoTest {
    @Test
    public void toString_incompleteTodo_includesTypeAndIncompleteStatus() {
        ToDo todo = new ToDo("read a book");

        assertEquals("[T][ ] read a book", todo.toString());
    }

    @Test
    public void representations_completedTodo_includeCompletedStatus() {
        ToDo todo = new ToDo("read a book");
        todo.markDone();

        assertEquals("[T][X] read a book", todo.toString());
        assertEquals("T | 1 | read a book", todo.toFileString());
    }
}
