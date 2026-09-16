package discitrack.command;

import discitrack.storage.Storage;
import discitrack.task.TaskList;
import discitrack.ui.Ui;

/**
 * Shows tasks whose descriptions contain a search keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command for the given keyword.
     *
     * @param keyword the keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays tasks that contain this command's keyword.
     *
     * @param tasks the task list to search.
     * @param ui the UI used to show matching tasks.
     * @param storage the storage used by the application.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showFoundTasks(tasks.findTasksByKeyword(keyword), tasks.asList());
    }
}
