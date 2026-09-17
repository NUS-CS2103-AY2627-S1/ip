package rocky.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the done state and display that every {@link Task} shares.
 * Task is abstract, so each test uses a minimal anonymous subclass.
 */
public class TaskTest {

    private static Task createTask(String description) {
        return new Task(description) { };
    }

    @Test
    public void newTask_notDoneWithEmptyCheckbox() {
        Task task = createTask("read book");

        assertFalse(task.isDone());
        assertEquals("[ ]", task.getCheckbox());
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void mark_notDoneTask_becomesDone() {
        Task task = createTask("read book");
        task.mark();

        assertTrue(task.isDone());
        assertEquals("[x]", task.getCheckbox());
    }

    @Test
    public void mark_alreadyDoneTask_staysDone() {
        Task task = createTask("read book");
        task.mark();
        task.mark();

        assertTrue(task.isDone());
    }

    @Test
    public void unmark_doneTask_becomesNotDone() {
        Task task = createTask("read book");
        task.mark();
        task.unmark();

        assertFalse(task.isDone());
        assertEquals("[ ]", task.getCheckbox());
    }

    @Test
    public void unmark_notDoneTask_staysNotDone() {
        Task task = createTask("read book");
        task.unmark();

        assertFalse(task.isDone());
    }

    @Test
    public void toString_notDoneAndDone_checkboxFollowedByDescription() {
        Task task = createTask("read book");
        assertEquals("[ ] read book", task.toString());

        task.mark();
        assertEquals("[x] read book", task.toString());
    }
}
