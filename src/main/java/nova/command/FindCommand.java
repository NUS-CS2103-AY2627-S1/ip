package nova.command;

import nova.storage.Storage;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for a keyword.
     *
     * @param keyword keyword to find.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds and displays tasks whose descriptions contain this command's keyword.
     *
     * @param tasks task list to search.
     * @param ui user interface used to display matching tasks.
     * @param storage unused because searching does not change persistent data.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.findTasksByDescription(keyword));
    }
}
