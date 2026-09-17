package jonathan.command;

import java.util.List;

import jonathan.storage.Storage;
import jonathan.task.Task;
import jonathan.task.TaskList;
import jonathan.ui.UI;

/** Sorts tasks according to their priority. */
public class SortCommand extends Command {

    /**
     * Displays a sorted view of the task list from high priority to low priority.
     *
     * @param tasks task list to sort
     * @param ui user interface used to display the sorted list
     * @param storage storage handler, not used by this display-only command
     */
    @Override
    public void execute(TaskList tasks, UI ui, Storage storage) {
        List<Task> sortedTasks = tasks.getTasksSortedByPriority();
        ui.showSortedTasks(sortedTasks);
    }
}
