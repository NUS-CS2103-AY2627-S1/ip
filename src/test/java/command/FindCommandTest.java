package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import exception.CbtException;
import task.TaskList;
import task.Todo;

/** Tests finding tasks by keywords in their descriptions. */
public class FindCommandTest {
    /** Verifies that matching tasks are renumbered and the task list is unchanged. */
    @Test
    public void execute_matchingKeyword_onlyMatchingTasksReturned() throws CbtException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Todo("write report"));
        tasks.addTask(new Todo("return book"));
        CommandResult result = new FindCommand("book").execute(tasks);

        assertTrue(result.message().contains("Matches on the radar:"));
        assertTrue(result.message().contains("1.[T][ ] read book"));
        assertTrue(result.message().contains("2.[T][ ] return book"));
        assertFalse(result.message().contains("write report"));
        assertFalse(result.isTaskListChanged());
        assertFalse(result.isExit());
    }

    /** Verifies that a blank keyword is rejected. */
    @Test
    public void execute_blankKeyword_exceptionThrown() {
        assertThrows(CbtException.class, () -> new FindCommand(" ").execute(new TaskList()));
    }

    /** Verifies a search with no matches returns only its heading. */
    @Test
    public void execute_noMatches_headingOnlyReturned() throws CbtException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("write report"));

        CommandResult result = new FindCommand("book").execute(tasks);

        assertEquals("Matches on the radar:", result.message());
        assertFalse(result.isTaskListChanged());
    }
}
