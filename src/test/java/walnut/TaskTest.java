package walnut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void constructor_createsIncompleteTask() {
        Task task = new Task("buy milk");

        assertEquals("buy milk", task.getDescription());
        assertFalse(task.isDone());
        assertEquals("[ ] buy milk", task.toString());
    }

    @Test
    void markAsDone_marksTaskComplete() {
        Task task = new Task("buy milk");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("[X] buy milk", task.toString());
    }

    @Test
    void markAsNotDone_marksTaskIncomplete() {
        Task task = new Task("buy milk");
        task.markAsDone();

        task.markAsNotDone();

        assertFalse(task.isDone());
    }

    @Test
    void constructor_rejectsNullDescription() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
    }

    @Test
    void constructor_rejectsBlankDescription() {
        assertThrows(IllegalArgumentException.class, () -> new Task("   "));
    }
}
