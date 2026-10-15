package pip.command;

import pip.storage.Storage;
import pip.task.TaskList;
import pip.ui.Ui;


/**
 * Represents a command that searches for tasks whose description contains a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a FindCommand that will search for the given keyword when executed.
     *
     * @param keyword The keyword to search for within task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds all tasks whose description contains the keyword and shows them
     * to the user. Does not modify the task list or storage.
     *
     * @param tasks The task list to search.
     * @param ui The UI used to display the matching tasks.
     * @param storage The current storage (unused).
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        TaskList matchingTasks = tasks.find(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
