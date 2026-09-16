package anaconda.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the ToDo type marker and inherited completion status in displayed tasks.
 */
public class ToDoTest {
    @Test
    public void toString_incompleteAndCompletedTask_showsTypeAndStatus() {
        ToDo task = new ToDo("borrow book");
        assertEquals("[T][ ] borrow book", task.toString());
        task.markAsDone();
        assertEquals("[T][X] borrow book", task.toString());
        task.markAsUndone();
        assertEquals("[T][ ] borrow book", task.toString());
    }
}
