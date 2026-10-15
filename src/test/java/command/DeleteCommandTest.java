package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import exception.CbtException;
import task.TaskList;
import task.Todo;

/** Tests deletion by the one-based task number entered by a user. */
public class DeleteCommandTest {
    /** Verifies that a valid one-based task number deletes and reports the selected task. */
    @Test
    public void execute_validTaskNumber_taskDeleted() throws CbtException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("first"));
        tasks.addTask(new Todo("second"));

        CommandResult result = new DeleteCommand("1").execute(tasks);

        assertEquals(1, tasks.getSize());
        assertEquals("[T][ ] second", tasks.getTask(0).toString());
        assertTrue(result.isTaskListChanged());
        assertTrue(result.message().contains("[T][ ] first"));
        assertTrue(result.message().contains("1 task remains in orbit."));
    }

    /** Verifies that an out-of-range task number leaves the list unchanged. */
    @Test
    public void execute_invalidTaskNumber_exceptionThrownAndListUnchanged() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("keep me"));

        assertThrows(CbtException.class, () -> new DeleteCommand("2").execute(tasks));
        assertEquals(1, tasks.getSize());
    }

    /** Verifies deleting from a longer list uses the plural remaining-task message. */
    @Test
    public void execute_threeTasks_pluralRemainingCountReturned() throws CbtException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("first"));
        tasks.addTask(new Todo("second"));
        tasks.addTask(new Todo("third"));

        CommandResult result = new DeleteCommand("2").execute(tasks);

        assertEquals(2, tasks.getSize());
        assertTrue(result.message().contains("2 tasks remain in orbit."));
    }

    /** Verifies deleting the final task reports that no tasks remain. */
    @Test
    public void execute_lastTask_zeroRemainingCountReturned() throws CbtException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("only"));

        CommandResult result = new DeleteCommand("1").execute(tasks);

        assertEquals(0, tasks.getSize());
        assertTrue(result.message().contains("0 tasks remain in orbit."));
    }
}
