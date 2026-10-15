package bob.command;

import bob.Storage;
import bob.Ui;
import bob.task.TaskList;

/**
 * Represents a request to display all tasks.
 */
public class ListCommand extends Command {
    /**
     * Displays a snapshot of all current tasks without modifying them.
     *
     * @param tasks task list to display
     * @param ui user interface used to display the list
     * @param storage storage, which is not accessed
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasks(tasks.asList());
    }
}
