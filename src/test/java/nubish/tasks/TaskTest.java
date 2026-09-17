package nubish.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for the status icon returned by {@link Task}.
 */
public class TaskTest {
    /**
     * Verifies that a new task starts as not done.
     */
    @Test
    public void getStatusIcon_newTask_returnsUndoneIcon() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that a loaded done task shows the done icon.
     */
    @Test
    public void getStatusIcon_savedDoneTask_returnsDoneIcon() {
        Task task = new Task(true, "read book");

        assertEquals("X", task.getStatusIcon());
    }

    /**
     * Verifies that a loaded undone task shows the undone icon.
     */
    @Test
    public void getStatusIcon_savedUndoneTask_returnsUndoneIcon() {
        Task task = new Task(false, "read book");

        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that marking a task changes its status icon to done.
     */
    @Test
    public void getStatusIcon_markedTask_returnsDoneIcon() {
        Task task = new Task("read book");

        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    /**
     * Verifies that unmarking a task changes its status icon to undone.
     */
    @Test
    public void getStatusIcon_unmarkedTask_returnsUndoneIcon() {
        Task task = new Task(true, "read book");

        task.unmarkAsDone();

        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that task descriptions can be retrieved.
     */
    @Test
    public void getDescription_validTask_returnsDescription() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
    }

    /**
     * Verifies that an undone task is saved in the expected file format.
     */
    @Test
    public void saveString_unmarkedTask_returnsCorrectFormat() {
        Task task = new Task("read book");

        assertEquals("0 | read book", task.saveString());
    }

    /**
     * Verifies that a done task is saved in the expected file format.
     */
    @Test
    public void saveString_markedTask_returnsCorrectFormat() {
        Task task = new Task(true, "read book");

        assertEquals("1 | read book", task.saveString());
    }

    /**
     * Verifies that a task is displayed in the expected user-facing format.
     */
    @Test
    public void toString_validTask_returnsFormattedTask() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
    }
}
