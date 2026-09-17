package nova.command;

import nova.storage.Storage;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    /**
     * Displays the current tasks in their list order.
     *
     * @param tasks task list to display.
     * @param ui user interface used to display the tasks.
     * @param storage unused because listing tasks does not change persistent data.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getTasks());
    }
}
