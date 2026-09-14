package penguin.command;

import penguin.storage.Storage;
import penguin.task.TaskList;
import penguin.ui.Ui;

/**
 * Represents a command that finds tasks containing a keyword.
 */
public class FindCommand extends Command {
    /** Keyword used to filter tasks. */
    private final String keyword;

    /**
     * Creates a keyword-search command.
     *
     * @param searchKeyword keyword or phrase to search for.
     */
    public FindCommand(String searchKeyword) {
        this.keyword = searchKeyword;
    }

    /**
     * Displays tasks whose descriptions contain the search keyword.
     *
     * @param tasks task list to search.
     * @param ui interface used for output.
     * @param storage storage used by the command framework.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        assert keyword != null : "Find command must contain a keyword";
        assert tasks != null : "Find command requires a task list";
        assert ui != null : "Find command requires a user interface";

        ui.showMatchingTasks(keyword, tasks.findMatchingTasks(keyword));
    }
}
