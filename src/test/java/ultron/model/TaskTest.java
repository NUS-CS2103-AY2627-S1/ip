package ultron.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the completion-status display of a task.
 */
class TaskTest {

    @Test
    void getDescription_taskCreated_returnsDescription() {
        Task task = new Task("read book", TaskType.TODO);

        assertEquals("read book", task.getDescription());
    }

    @Test
    void getType_taskCreated_returnsSuppliedType() {
        Task task = new Task("submit report", TaskType.DEADLINE);

        assertEquals(TaskType.DEADLINE, task.getType());
    }

    @Test
    void isDone_newTask_false() {
        Task task = new Task("read book", TaskType.TODO);

        assertFalse(task.isDone());
    }

    @Test
    void isDone_taskMarkedDone_true() {
        Task task = new Task("read book", TaskType.TODO);
        task.markAsDone();

        assertTrue(task.isDone());
    }

    @Test
    void isDone_taskUnmarkedAfterDone_false() {
        Task task = new Task("read book", TaskType.TODO);
        task.markAsDone();
        task.markAsUndone();

        assertFalse(task.isDone());
    }

    @Test
    void getStatusIcon_newTask_spaceIcon() {
        Task task = new Task("read book", TaskType.TODO);

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void getStatusIcon_taskMarkedDone_xIcon() {
        Task task = new Task("read book", TaskType.TODO);
        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void getStatusIcon_taskUnmarkedAfterDone_spaceIcon() {
        Task task = new Task("read book", TaskType.TODO);
        task.markAsDone();
        task.markAsUndone();

        assertEquals(" ", task.getStatusIcon());
    }
}
