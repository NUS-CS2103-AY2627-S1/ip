package potato.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import potato.exception.PotatoException;
import potato.storage.Storage;
import potato.task.Task;
import potato.task.Todo;
import potato.tasklist.TaskList;
import potato.ui.Ui;

/**
 * Contains unit tests for checking that {@link FindCommand} reports search results through {@link Ui}.
 */
public class FindCommandTest {

    /**
     * Tests that matching find results are written to the UI response buffer for GUI display.
     *
     * @throws PotatoException If command execution fails unexpectedly.
     */
    @Test
    public void executeMatchingKeywordWritesResultsToUi() throws PotatoException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("prep vegetables"));
        tasks.add(new Todo("wash dishes"));
        Ui ui = new Ui();

        new FindCommand("prep").execute(new TaskList(tasks), ui, new Storage("unused.txt"));

        String response = ui.getAndClearResponse();
        assertTrue(response.contains("Here are the matching tasks in your list:"));
        assertTrue(response.contains("1.[T][ ] prep vegetables"));
    }

    /**
     * Tests that a failed search is written to the UI response buffer for GUI display.
     *
     * @throws PotatoException If command execution fails unexpectedly.
     */
    @Test
    public void executeMissingKeywordWritesNoMatchMessageToUi() throws PotatoException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("prep vegetables"));
        Ui ui = new Ui();

        new FindCommand("rice").execute(new TaskList(tasks), ui, new Storage("unused.txt"));

        String response = ui.getAndClearResponse();
        assertTrue(response.contains("No matching tasks found in your list."));
    }
}
