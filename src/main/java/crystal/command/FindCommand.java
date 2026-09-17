package crystal.command;

import java.util.List;

import crystal.storage.Storage;
import crystal.task.Task;
import crystal.task.TaskList;
import crystal.ui.Ui;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public final class FindCommand extends Command {
    /** Keyword to search for in task descriptions. */
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for a keyword.
     *
     * @param keyword nonblank keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays matching tasks without changing persistent data.
     *
     * @param tasks task list to search.
     * @param ui console interface used to present the result.
     * @param storage persistence service; not used by this read-only command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.findTasks(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
