package habpyduck.command;

import habpyduck.storage.Storage;
import habpyduck.task.TaskList;
import habpyduck.ui.Ui;

/**
 * Shows the current task list.
 */
public class ListCommand extends Command {
    /**
     * Reloads tasks from storage and prints the task list.
     *
     * @param tasks the task list to refresh.
     * @param ui the UI used to show command results.
     * @param storage the storage used to load tasks.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.replaceAll(storage.loadTasks(true, ui));
        ui.showTaskList(tasks.asList());
    }
}
