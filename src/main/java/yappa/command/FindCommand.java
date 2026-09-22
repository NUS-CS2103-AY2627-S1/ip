package yappa.command;

import yappa.exception.YappaException;
import yappa.task.TaskList;
import yappa.ui.Ui;

/** Finds tasks whose descriptions contain a search query. */
public class FindCommand extends Command {
    private final String searchQuery;

    /**
     * Creates a find command for the specified query.
     *
     * @param searchQuery Text to search for.
     */
    public FindCommand(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    /**
     * Finds matching tasks and formats the result.
     *
     * @param tasks Task list to search.
     * @param ui    User interface used to format the response.
     * @return Formatted search response.
     * @throws YappaException If searching fails.
     */
    @Override
    public String execute(TaskList tasks, Ui ui) throws YappaException {
        TaskList matchedTasks = tasks.find(searchQuery);
        return ui.showMatchingTasks(matchedTasks);
    }

    /** Returns whether this command modifies the task list. */
    @Override
    public boolean modifiesTasks() {
        return false;
    }
}
