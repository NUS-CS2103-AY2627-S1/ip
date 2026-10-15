package clearblue.command;

import java.util.List;

import clearblue.storage.Storage;
import clearblue.task.Task;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Prints the tasks whose description contains a search keyword, or reports
 * that none match.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for tasks by description.
     *
     * @param keyword text to search for within each task's description
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.getTasksContaining(keyword);
        if (matches.isEmpty()) {
            ui.showNoMatchingTasks(keyword);
        } else {
            ui.showMatchingTasks(matches);
        }
    }
}
