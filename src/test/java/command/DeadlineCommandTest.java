package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import exception.CbtException;
import task.TaskList;

/** Tests parsing and creation of deadline tasks. */
public class DeadlineCommandTest {
    /** Verifies that valid deadline details add a deadline and report a state change. */
    @Test
    public void execute_validDetails_deadlineAdded() throws CbtException {
        TaskList tasks = new TaskList();

        CommandResult result = new DeadlineCommand("return book /by 2/12/2019 1800").execute(tasks);

        assertEquals(1, tasks.getSize());
        assertEquals("DEADLINE | 0 | return book | 02/12/2019 1800",
                tasks.getTask(0).toFileFormat());
        assertTrue(result.isTaskListChanged());
    }

    /** Verifies that a missing deadline description or date is rejected. */
    @Test
    public void execute_missingDescriptionOrDate_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(CbtException.class, () -> new DeadlineCommand("/by 2/12/2019").execute(tasks));
        assertThrows(CbtException.class, () -> new DeadlineCommand("return book /by ").execute(tasks));
        assertThrows(CbtException.class, () -> new DeadlineCommand(
                "return /by book /by 2/12/2019").execute(tasks));
    }

    /** Verifies that an invalid deadline date is rejected without changing the list. */
    @Test
    public void execute_invalidDate_exceptionThrownAndListUnchanged() {
        TaskList tasks = new TaskList();

        assertThrows(CbtException.class, () -> new DeadlineCommand("return book /by tomorrowish").execute(tasks));
        assertEquals(0, tasks.getSize());
    }
}
