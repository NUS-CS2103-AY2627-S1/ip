package lizzy.command;

import lizzy.storage.Storage;
import lizzy.task.TaskList;
import lizzy.ui.Ui;

/**
 * Displays tasks whose descriptions contain a requested keyword.
 */
public class FindCommand extends Command {
    /** The keyword to search for. */
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the supplied keyword.
     *
     * @param keyword the keyword to find
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds matching task descriptions and displays them without changing the task list.
     *
     * @param tasks the current task list
     * @param ui the component used for user interaction
     * @param storage unused because finding does not change persisted data
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks, tasks.findMatchingTaskNumbers(keyword));
    }
}
