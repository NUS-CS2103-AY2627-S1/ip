package jonathan.command;

import jonathan.storage.Storage;
import jonathan.task.Priority;
import jonathan.task.TaskList;
import jonathan.ui.UI;

/** Displays tasks that have a specified priority. */
public class FilterPriorityCommand extends Command {
    private final Priority priority;

    /**
     * Creates a command that filters tasks by priority.
     *
     * @param priority priority to filter by
     */
    public FilterPriorityCommand(Priority priority) {
        this.priority = priority;
    }

    /**
     * Executes the priority filter without changing the task list.
     *
     * @param tasks task list to search
     * @param ui user interface used to display matching tasks
     * @param storage storage handler, not used by this command
     */
    @Override
    public void execute(TaskList tasks, UI ui, Storage storage) {
        ui.showFilteredTasks(priority, tasks.getTasksByPriority(priority));
    }
}
