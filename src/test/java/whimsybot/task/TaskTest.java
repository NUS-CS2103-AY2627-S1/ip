package whimsybot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the common state and display behavior of {@link Task}. */
class TaskTest {
    @Test
    void newTask_hasIncompleteStatusAndDescription() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("read book", task.getDescription());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void markAndUnmarkTask_updatesCompletionStateAndIcon() {
        Task task = new Task("read book");

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());

        task.unmarkAsDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }
}
