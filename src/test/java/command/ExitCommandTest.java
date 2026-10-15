package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import task.TaskList;

/** Tests the result used to terminate the application. */
public class ExitCommandTest {
    /** Verifies that exiting returns the exit flag without modifying tasks. */
    @Test
    public void execute_anyTaskList_exitResultReturnedWithoutMutation() {
        TaskList tasks = new TaskList();

        CommandResult result = new ExitCommand().execute(tasks);

        assertEquals("Mission paused. Safe travels!", result.message());
        assertFalse(result.isTaskListChanged());
        assertTrue(result.isExit());
        assertEquals(0, tasks.getSize());
    }
}
