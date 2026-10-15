package jassabot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the concrete base task's default persistence format and repeated state transitions.
 */
public class TaskTest {
    @Test
    public void toDataString_baseTask_usesTodoFormatAndEscapesDescription() {
        Task task = new Task("compare | \\ C");
        assertEquals("compare | \\ C", task.getDescription());
        assertEquals("[ ] compare | \\ C", task.toString());
        assertEquals("T | 0 | compare \\| \\\\ C", task.toDataString());
        task.markAsDone();
        assertEquals("T | 1 | compare \\| \\\\ C", task.toDataString());
    }

    @Test
    public void markAndUnmark_repeatedTransitions_areIdempotent() {
        Task task = new Task("read");
        task.markAsUndone();
        assertFalse(task.isDone());
        task.markAsDone();
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        task.markAsUndone();
        task.markAsUndone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }
}
