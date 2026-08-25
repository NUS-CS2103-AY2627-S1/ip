package matthew.command;

import matthew.storage.Storage;
import matthew.task.TaskList;
import matthew.ui.Ui;

/** Finds tasks whose descriptions contain a supplied keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for a keyword.
     *
     * @param keyword Keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays tasks whose descriptions contain the command's keyword.
     *
     * @param tasks Task list to search.
     * @param ui User interface used to display the matching tasks.
     * @param storage Unused storage supplied by the command loop.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.getTasksContaining(keyword));
    }
}
