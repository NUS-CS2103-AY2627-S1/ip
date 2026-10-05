package gatsby.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the state, display, and storage representations of a {@link Task}. */
class TaskTest {

    /** Verifies the initial state and description of a newly created task. */
    @Test
    void constructor_newTask_startsUnfinishedWithDescription() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals("read book", task.getTaskName());
        assertEquals("0", task.getStatusFlag());
        assertEquals("[ ] read book", task.toString());
        assertEquals("0 | read book", task.toFileFormat());
    }

    /** Verifies that marking a task done changes all relevant representations. */
    @Test
    void markDone_completedTask_hasDoneRepresentations() {
        Task task = new Task("read book");

        task.markDone();

        assertTrue(task.isDone());
        assertEquals("1", task.getStatusFlag());
        assertEquals("[X] read book", task.toString());
        assertEquals("1 | read book", task.toFileFormat());
    }

    /** Verifies that an already completed task can be returned to unfinished. */
    @Test
    void markUndone_completedTask_becomesUnfinished() {
        Task task = new Task("read book");
        task.markDone();

        task.markUndone();

        assertFalse(task.isDone());
        assertEquals("0", task.getStatusFlag());
        assertEquals("[ ] read book", task.toString());
        assertEquals("0 | read book", task.toFileFormat());
    }
}
