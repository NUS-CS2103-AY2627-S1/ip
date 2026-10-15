package gongrilla.command;

import gongrilla.storage.Storage;
import gongrilla.task.TaskList;
import gongrilla.ui.Ui;

/**
 * Finds tasks whose descriptions contain a specified keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions.
     *
     * @param keyword text to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds and displays matching tasks without changing the task list or storage.
     *
     * @param tasks task list to search.
     * @param ui user interface used to display matches.
     * @param storage task storage; unused because searching does not persist changes.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
