package clearblue.command;

import clearblue.storage.Storage;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Prints all tasks in the list, or reports that the list is empty.
 */
public class ListCommand extends Command {
    /**
     * Prints every task in {@code tasks}, or reports that it is empty.
     * Reads only; does not modify or save {@code tasks}.
     *
     * @param tasks task list to print
     * @param ui user interface to report through
     * @param storage unused
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.isEmpty()) {
            ui.showEmptyList();
        } else {
            ui.showTaskList(tasks);
        }
    }
}
