package meka.command;

import meka.storage.Storage;
import meka.task.TaskList;
import meka.ui.Ui;

/**
 * Displays tasks whose descriptions contain the supplied keyword.
 */
public class FindCommand extends Command {
    /** Description text to match, ignoring letter case. */
    private final String keyword;

    /**
     * Creates a command that searches task descriptions.
     *
     * @param keyword Text to search for in each description.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Shows matching tasks without changing the task list or saved data.
     *
     * @param tasks Task list to search.
     * @param ui User interface through which matches are displayed.
     * @param storage Current storage component; not modified.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
