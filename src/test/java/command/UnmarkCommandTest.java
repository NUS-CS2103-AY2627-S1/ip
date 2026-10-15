package command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import exception.CbtException;
import task.TaskList;
import task.Todo;

/** Tests changing completed tasks back to incomplete. */
public class UnmarkCommandTest {
    /** Verifies that a completed task can be unmarked and the change is reported. */
    @Test
    public void execute_completedTask_taskUnmarked() throws CbtException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.addTask(todo);

        CommandResult result = new UnmarkCommand("1").execute(tasks);

        assertFalse(todo.isDone());
        assertTrue(result.isTaskListChanged());
        assertTrue(result.message().contains("[T][ ] read book"));
    }

    /** Verifies that an invalid task number cannot be unmarked. */
    @Test
    public void execute_invalidTaskNumber_exceptionThrown() {
        assertThrows(CbtException.class, () -> new UnmarkCommand("1").execute(new TaskList()));
    }

    /** Verifies that unmarking an already incomplete task reports an error. */
    @Test
    public void execute_alreadyIncompleteTask_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        assertThrows(CbtException.class, () -> new UnmarkCommand("1").execute(tasks));
    }
}
