package yqr.command;

import yqr.storage.Storage;
import yqr.task.TaskList;
import yqr.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for the given keyword.
     *
     * @param keyword keyword to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds and displays tasks with descriptions containing the keyword.
     *
     * @param tasks task list to search.
     * @param ui user interface used to display matching tasks.
     * @param storage storage component, which this command does not modify.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.findTasks(keyword));
    }
}
