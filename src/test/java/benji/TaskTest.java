package benji;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void markAndUnmarkTask_changesStatusAndDisplay() {
        Todo task = new Todo("submit assignment");

        assertFalse(task.isDone());
        assertEquals("[T][ ] submit assignment", task.toString());

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("[T][X] submit assignment", task.toString());

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals("[T][ ] submit assignment", task.toString());
    }
}
