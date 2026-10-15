package jason.command;

import jason.storage.Storage;
import jason.task.TaskList;
import jason.ui.Ui;

/**
 * Finds and displays tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command for the supplied keyword.
     *
     * @param keyword text to search for.
     * @throws IllegalArgumentException if the keyword is null or blank.
     */
    public FindCommand(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("A search keyword is required.");
        }
        this.keyword = keyword.trim();
    }

    /**
     * Searches the task list and displays the matching tasks.
     *
     * @param tasks task list to search.
     * @param ui user interface used to display the results.
     * @param storage storage, which is unused because searching does not mutate tasks.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
