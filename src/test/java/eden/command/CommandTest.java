package eden.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import eden.exception.EdenException;
import eden.task.TaskList;
import eden.task.Todo;
import eden.ui.Ui;

/**
 * Tests each concrete command independently of Eden's command routing.
 */
public class CommandTest {
    private final Ui ui = new Ui();

    /**
     * Verifies that exit commands return the farewell and request application exit.
     */
    @Test
    public void exitCommand_execute_returnsFarewellAndRequestsExit() {
        ExitCommand command = new ExitCommand();

        assertEquals("Bye. Hope to see you again soon!", command.execute(ui));
        assertTrue(command.isExit());
    }

    /**
     * Verifies that list commands format every supplied task without exiting.
     */
    @Test
    public void listCommand_execute_formatsAllTasks() {
        TaskList tasks = new TaskList(List.of(
                new Todo("first task"), new Todo("second task")));
        ListCommand command = new ListCommand(tasks);

        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] first task\n"
                + "2.[T][ ] second task", command.execute(ui));
        assertFalse(command.isExit());
    }

    /**
     * Verifies that find commands format matching tasks and reject blank keywords.
     */
    @Test
    public void findCommand_execute_formatsMatchesAndRejectsBlankKeyword()
            throws EdenException {
        TaskList tasks = new TaskList(List.of(
                new Todo("Read book"), new Todo("write report")));

        assertEquals("Here are the matching tasks in your list:\n"
                + "1.[T][ ] Read book", new FindCommand("book", tasks).execute(ui));

        EdenException exception = assertThrows(EdenException.class, () ->
                new FindCommand("   ", tasks).execute(ui));
        assertEquals("OOPS!!! The keyword for find cannot be empty.",
                exception.getMessage());
    }
}
