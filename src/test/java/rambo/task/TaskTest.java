package rambo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import rambo.exception.RamboException;

/**
 * Tests the name, completion state, and text representations of {@link Task}.
 */
public class TaskTest {
    @Test
    void constructor_taskName_createsIncompleteTaskWithName() {
        Task task = new Task("buy milk");

        assertEquals("buy milk", task.getTaskName());
        assertFalse(task.isDone());
        assertFalse(task.hasPriority());
        assertEquals(Task.NO_PRIORITY, task.getPriorityLevel());
    }

    @Test
    void toggleDone_incompleteTask_marksTaskDoneAndReturnsTrue() {
        Task task = new Task("buy milk");

        assertTrue(task.toggleDone());
        assertTrue(task.isDone());
    }

    @Test
    void toggleDone_completedTask_marksTaskIncompleteAndReturnsFalse() {
        Task task = new Task("buy milk");
        task.toggleDone();

        assertFalse(task.toggleDone());
        assertFalse(task.isDone());
    }

    @Test
    void toDataString_incompleteTask_returnsStorageFormat() {
        Task task = new Task("buy milk");

        assertEquals("T||buy milk|0", task.toDataString());
    }

    @Test
    void toString_completedTask_returnsDisplayFormat() {
        Task task = new Task("buy milk");
        task.toggleDone();

        assertEquals("[T][X] buy milk", task.toString());
    }

    @Test
    void setPriorityLevel_validLevel_updatesStorageAndDisplayFormats() {
        Task task = new Task("buy milk");

        task.setPriorityLevel(1);

        assertTrue(task.hasPriority());
        assertEquals(1, task.getPriorityLevel());
        assertEquals("T||buy milk|1", task.toDataString());
        assertEquals("[T][][P1] buy milk", task.toString());
    }

    @Test
    void setPriorityLevel_outOfRange_throwsRamboException() {
        Task task = new Task("buy milk");

        assertThrows(RamboException.class, () -> task.setPriorityLevel(0));
        assertThrows(RamboException.class, () -> task.setPriorityLevel(4));
    }
}
