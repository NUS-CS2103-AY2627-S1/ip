package sine.command;

import sine.storage.Storage;
import sine.task.TaskList;
import sine.ui.Ui;

/**
 * Displays all tasks currently stored in the task list.
 */
public class ListCommand extends Command {
    /**
     * Shows all tasks without changing the task list.
     *
     * @param tasks current task list
     * @param ui console user interface
     * @param storage task persistence service
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
