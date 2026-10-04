package penny.command;

import java.util.List;

import penny.storage.Storage;
import penny.task.Task;
import penny.task.TaskList;
import penny.ui.Ui;

/**
 * Represents a command to search and list all tasks whose description matches a keyword.
 */
public class FindCommand extends Command {

    private final String keyword;

    /**
     * Constructs a FindCommand with the target search keyword.
     *
     * @param keyword The keyword used to filter tasks.
     */
    public FindCommand(String keyword) {
        assert keyword != null && !keyword.isEmpty() : "Search keyword must not be empty";
        this.keyword = keyword;
    }

    /**
     * Executes the keyword search command by finding matching tasks
     * and displaying them through the user interface.
     *
     * @param tasks The task list searched for matching tasks.
     * @param ui The user interface used to display matching results.
     * @param storage The storage handler (not modified by this command).
     * @return The matching tasks string from the user interface.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        assert tasks != null : "TaskList must not be null";
        assert ui != null : "Ui must not be null";
        assert storage != null : "Storage must not be null";
        assert this.keyword != null && !this.keyword.isEmpty() : "Search keyword must not be empty";

        List<Task> matchingTasks = tasks.findTasksByKeyword(this.keyword);
        return ui.showMatchingTasks(matchingTasks, this.keyword);
    }
}
