package allmind.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the common behavior shared by all task types. */
public class TaskTest {
    @Test
    public void constructor_validDescription_createsUnfinishedTask() {
        Task task = new Task("read notes");

        assertEquals("read notes", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read notes", task.toString());
    }

    @Test
    public void markAsDone_thenMarkAsNotDone_updatesStatus() {
        Task task = new Task("read notes");

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read notes", task.toString());

        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    public void constructor_nullDescription_assertionThrown() {
        assertThrows(AssertionError.class, () -> new Task(null));
    }
}
