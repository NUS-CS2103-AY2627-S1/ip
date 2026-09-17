package megatron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the shared description, status, and display behavior of {@link Task}.
 */
class TaskTest {
    @Test
    void constructor_description_createsNotDoneBaseTask() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
        assertEquals("T", task.getTypeCode());
        assertEquals("", task.getExtra());
        assertEquals("[ ] read book", task.toString());
        assertEquals("[-][ ] read book", task.displayText());
    }

    @Test
    void markAsDone_notDoneTask_changesStatusAndDisplay() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("[X] read book", task.toString());
        assertEquals("[-][X] read book", task.displayText());
    }

    @Test
    void markAsNotDone_doneTask_changesStatusAndDisplay() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals("[ ] read book", task.toString());
        assertEquals("[-][ ] read book", task.displayText());
    }
}
