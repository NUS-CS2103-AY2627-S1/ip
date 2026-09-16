package turtley.command;

import java.util.Locale;

import turtley.model.Task;
import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Displays tasks whose tags contain a supplied case-insensitive substring.
 */
public class FilterCommand extends Command {

    private final String query;

    /**
     * Creates a filter command.
     *
     * @param query the tag substring to search for.
     */
    public FilterCommand(String query) {
        this.query = query;
    }

    /**
     * Displays tasks with matching tags in their original task-list order.
     *
     * @param tasks the application's task list.
     * @param ui the application's user interface.
     * @param storage the application's persistence service, which is not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String parsedQuery = TagParser.parseFilterQuery(query);
        String normalizedQuery = parsedQuery.toLowerCase(Locale.ROOT);
        boolean hasMatchingTask = false;
        ui.showFilterHeader(parsedQuery);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean matches = task.getTags().stream()
                    .map(tag -> tag.toLowerCase(Locale.ROOT))
                    .anyMatch(tag -> tag.contains(normalizedQuery));
            if (matches) {
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
