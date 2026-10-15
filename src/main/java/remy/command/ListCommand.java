package remy.command;

import remy.storage.Storage;
import remy.task.TaskList;
import remy.ui.Ui;

/**
 * Displays every task in the task list.
 *
 * @author LEE-wz
 */
public class ListCommand extends Command {
    /**
     * Perform list command
     *
     * @param tasks task collection to operate on
     * @param ui user interface used to display results
     * @param storage persistence service used to save changes
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getTasks());
    }
}
