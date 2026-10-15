package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import exception.CbtException;
import task.Deadline;
import task.TaskList;

/** Tests filtering displayed tasks by date. */
public class ListDateCommandTest {
    /** Verifies that only tasks occurring on the requested date are returned. */
    @Test
    public void execute_matchingAndNonMatchingTasks_onlyMatchingTaskReturned() throws CbtException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Deadline("matching", LocalDateTime.of(2026, 8, 26, 12, 0)));
        tasks.addTask(new Deadline("different", LocalDateTime.of(2026, 8, 27, 12, 0)));
        CommandResult result = new ListDateCommand("2026-08-26").execute(tasks);

        assertTrue(result.message().contains("On the radar for Aug 26 2026"));
        assertTrue(result.message().contains("matching"));
        assertFalse(result.message().contains("different"));
        assertFalse(result.isTaskListChanged());
    }

    /** Verifies that blank and invalid dates are rejected. */
    @Test
    public void execute_blankOrInvalidDate_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(CbtException.class, () -> new ListDateCommand(" ").execute(tasks));
        assertThrows(CbtException.class, () -> new ListDateCommand("31/02/2026").execute(tasks));
    }

    /** Verifies a date with no matching tasks returns only the dated heading. */
    @Test
    public void execute_noMatches_headingOnlyReturned() throws CbtException {
        CommandResult result = new ListDateCommand("2026-08-26").execute(new TaskList());

        assertEquals("On the radar for Aug 26 2026:", result.message());
        assertFalse(result.isTaskListChanged());
    }
}
