package remy.command;

import remy.storage.Storage;
import remy.task.TaskList;
import remy.ui.Ui;

/**
 * Displays tasks whose descriptions contain a user-provided keyword.
 */
public class FindCommand extends Command {
    /** The keyword used to match task descriptions. */
    private final String keyword;

    /**
     * Creates a command that finds tasks containing the given keyword.
     *
     * @param keyword keyword to search for in task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Perform find command
     *
     * @param tasks task collection to operate on
     * @param ui user interface used to display results
     * @param storage persistence service used to save changes
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
