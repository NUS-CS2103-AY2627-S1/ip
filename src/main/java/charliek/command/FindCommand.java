package charliek.command;

import java.util.List;
import java.util.Locale;

import charliek.model.Task;
import charliek.model.TaskList;
import charliek.ui.Ui;

/**
 * Finds and displays tasks that match a given keyword in their description.
 */
public class FindCommand extends Command {
    /**
     * The task list to search.
     */
    private final TaskList tasks;

    /**
     * The UI used to show the matching tasks.
     */
    private final Ui ui;

    /**
     * The keyword to search for in task descriptions.
     */
    private final String keyword;

    /**
     * Creates a find command.
     *
     * @param tasks the task list to search.
     * @param ui the UI used to show the matching tasks.
     * @param keyword the keyword to search for in task descriptions.
     */
    public FindCommand(TaskList tasks, Ui ui, String keyword) {
        this.tasks = tasks;
        this.ui = ui;
        this.keyword = keyword;
    }

    /**
     * Displays all tasks whose descriptions contain the search keyword.
     */
    @Override
    public void execute() {
        List<Task> matchingTasks = findMatchingTasks();
        ui.showMatchingTasks(matchingTasks);
    }

    /**
     * Searches for tasks whose description contains the keyword.
     *
     * @return a list of tasks that match the keyword.
     */
    private List<Task> findMatchingTasks() {
        return tasks.toList().stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(keyword.toLowerCase(Locale.ROOT)))
                .toList();
    }
}


