package thomas.command;

import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Displays all tasks in the task list.
 */
public class ListCommand extends Command {
    /**
     * Prints every task currently stored in the list.
     *
     * @param tasks task list to display
     * @param ui user interface used for output
     * @param storage unused for this command
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasks(tasks);
    }
}
