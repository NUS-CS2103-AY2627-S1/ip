package turtley.command;

import java.util.Locale;

import turtley.exception.TurtleyException;
import turtley.model.Task;
import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Displays tasks whose descriptions contain a supplied keyword.
 */
public class FindCommand extends Command {

    private final String keyword;

    /**
     * Creates a find command.
     *
     * @param keyword the keyword to search for in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays matching tasks in their original task-list order.
     *
     * @param tasks the application's task list.
     * @param ui the application's user interface.
     * @param storage the application's persistence service, which is not used.
     * @throws TurtleyException if no keyword was supplied.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (keyword == null || keyword.isBlank()) {
            throw new TurtleyException("Invalid format. Use: find <keyword>");
        }

        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        boolean hasMatchingTask = false;
        ui.showFindHeader();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean matchesDescription = task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword);
            boolean matchesTag = task.getTags().stream()
                    .map(tag -> tag.toLowerCase(Locale.ROOT))
                    .anyMatch(tag -> tag.contains(normalizedKeyword));
            if (matchesDescription || matchesTag) {
                hasMatchingTask = true;
                ui.showTask(i, task);
            }
        }
        if (!hasMatchingTask) {
            ui.showNoMatchingTasks();
        }
        ui.showSeparator();
    }
}
