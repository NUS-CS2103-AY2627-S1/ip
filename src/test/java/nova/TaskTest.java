package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void newTask_isNotDone() {
        Task task = new Task("read book");
        assertFalse(task.isDone());
    }

    @Test
    public void markDone_thenIsDone() {
        Task task = new Task("read book");
        task.markDone();
        assertTrue(task.isDone());
    }

    @Test
    public void markUndone_afterMarkDone_isNotDone() {
        Task task = new Task("read book");
        task.markDone();
        task.markUndone();
        assertFalse(task.isDone());
    }

    @Test
    public void toString_undoneTask_showsEmptyIcon() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void toString_doneTask_showsCrossIcon() {
        Task task = new Task("read book");
        task.markDone();
        assertEquals("[X] read book", task.toString());
    }
}
