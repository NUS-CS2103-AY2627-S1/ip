package shrekanddonkey.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests task operations and string representations of {@link Todo}.
 */
public class TodoTest {

    @Test
    public void toString_newTodo_formattedWithTodoMarker() {
        Todo todo = new Todo("borrow book");
        assertEquals("[T][ ] borrow book", todo.toString());
    }

    @Test
    public void toString_completedTodo_formattedWithDoneMarker() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("[T][X] borrow book", todo.toString());
    }
}
