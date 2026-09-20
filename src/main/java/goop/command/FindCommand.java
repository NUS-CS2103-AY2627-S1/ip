package goop.command;

import goop.storage.Storage;
import goop.task.TaskList;
import goop.ui.Ui;

/**
 * Represents a request to display tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the given keyword.
     *
     * @param keyword Text to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays matching tasks without changing or saving the task list.
     *
     * @param tasks Task list to search.
     * @param ui User interface used to display matching tasks.
     * @param storage Task storage, which is not accessed.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
