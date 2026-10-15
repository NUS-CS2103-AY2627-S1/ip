package larry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
public class TaskTest {

    @Test
    public void markAsDone_taskNotDone_changesStatusToDone() {
        Task task = new Todo("read book");
        task.markAsDone();
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void markAsNotDone_taskDone_changesStatusToNotDone() {
        Task task = new Todo("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void newTodo_initiallyNotDone() {
        Task task = new Todo("read book");
        assertFalse(task.isDone());
        assertEquals("read book", task.getDescription());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void markAsDone_twice_remainsDone() {
        Task task = new Todo("read book");
        task.markAsDone();
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_twice_remainsNotDone() {
        Task task = new Todo("read book");
        task.markAsNotDone();
        task.markAsNotDone();
        assertFalse(task.isDone());
    }
}
