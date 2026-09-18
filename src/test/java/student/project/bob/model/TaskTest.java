package student.project.bob.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests a task's description and completion-state behavior.
 */
public class TaskTest {
    /**
     * Verifies that a task starts incomplete and displays its description.
     */
    @Test
    public void constructor_task_startsIncompleteWithDescription() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }

    /**
     * Verifies that marking a task done changes its state and display.
     */
    @Test
    public void setDone_incompleteTask_marksTaskComplete() {
        Task task = new Task("read book");

        task.setDone();
        task.setDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());
    }

    /**
     * Verifies that unmarking a completed task restores its incomplete state and display.
     */
    @Test
    public void setUndone_completeTask_marksTaskIncomplete() {
        Task task = new Task("read book");
        task.setDone();

        task.setUndone();
        task.setUndone();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }
}
