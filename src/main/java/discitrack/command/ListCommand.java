package discitrack.command;

import discitrack.storage.Storage;
import discitrack.task.TaskList;
import discitrack.ui.Ui;

/**
 * Shows all tasks currently in the task list.
 */
public class ListCommand extends Command {
    /**
     * Displays the current task list.
     *
     * @param tasks the task list to display.
     * @param ui the UI used to show the task list.
     * @param storage the storage used by the application.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.asList());
    }
}
