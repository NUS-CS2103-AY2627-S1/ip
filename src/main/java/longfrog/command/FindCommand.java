package longfrog.command;

import java.util.List;
import java.util.Locale;

import longfrog.task.Task;
import longfrog.task.TaskList;
import longfrog.ui.Ui;

/** Finds tasks whose descriptions contain a requested keyword. */
public class FindCommand implements Command {
    private final TaskList taskList;
    private final String keyword;
    private final String normalizedKeyword;

    /**
     * Creates a find command for a task-description keyword.
     *
     * @param taskList the tasks to search
     * @param keyword the case-insensitive description substring to find
     */
    public FindCommand(TaskList taskList, String keyword) {
        this.taskList = taskList;
        this.keyword = keyword;
        this.normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
    }

    /** Displays matching tasks in their original task-list order. */
    @Override
    public boolean execute(Ui ui) {
        List<Task> matchingTasks = taskList.getTasks().stream()
                .filter(task -> task.getName().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();

        if (matchingTasks.isEmpty()) {
            ui.showMessage("No ripples for “" + keyword + "”.");
        } else {
            String rippleLabel = matchingTasks.size() == 1 ? "ripple" : "ripples";
            ui.showMessage("Found " + matchingTasks.size() + " matching " + rippleLabel + ":");
            ui.showNumberedTasks(matchingTasks);
        }

        return false;
    }
}
