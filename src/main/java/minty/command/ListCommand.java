package minty.command;

import minty.storage.Storage;
import minty.task.TaskList;
import minty.ui.Ui;

/**
 * Displays every task currently in Minty's task list.
 */
public class ListCommand extends Command {

    /**
     * Shows all tasks in their current list order.
     *
     * @param tasks task list to display.
     * @param ui command-line interface used for the response.
     * @param storage task storage, which is not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
