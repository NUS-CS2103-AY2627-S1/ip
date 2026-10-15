package sage.command;

import java.util.List;

import sage.core.TaskList;
import sage.storage.Storage;
import sage.task.Task;
import sage.ui.Ui;

/**
 * Finds tasks whose descriptions contain a specified keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for the specified keyword.
     *
     * @param keyword the keyword to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds matching tasks and displays them to the user.
     *
     * @param tasks the task list to search.
     * @param ui the user interface used to display matching tasks.
     * @param storage the storage system, which is not used by this command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.find(keyword);
        ui.showMatchingTasks(matches, tasks.getTasks());
    }
}
