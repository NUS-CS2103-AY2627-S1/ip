package bob.command;

import java.util.List;
import java.util.Map;

import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Represents a command to search for tasks containing a specific keyword.
 */
public class FindCommand extends Command {

    private final String keyword;

    /**
     * Constructs a FindCommand with the specified search keyword.
     *
     * @param keyword the keyword to search for
     */
    public FindCommand(String keyword) {
        assert keyword != null && !keyword.isBlank() : "Search keyword should not be null or blank";
        this.keyword = keyword;
    }

    /**
     * Executes the find command by searching the task list and displaying matching
     * tasks.
     *
     * @param tasks   the list of all tasks
     * @param ui      the user interface handler
     * @param storage the storage handler
     * @throws BobException if an error occurs during execution
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage<TaskList> storage) throws BobException {
        assertExecutionDependencies(tasks, ui, storage);
        List<Map.Entry<Integer, Task>> matchingEntries = tasks.find(keyword);
        ui.setMatchingTasks(matchingEntries);
    }
}
