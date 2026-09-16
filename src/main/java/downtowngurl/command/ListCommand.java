package downtowngurl.command;

import downtowngurl.storage.Storage;
import downtowngurl.task.TaskList;
import downtowngurl.ui.Ui;

/**
 * Represents the command that shows all tasks.
 */
public class ListCommand extends Command {
    /**
     * Shows all tasks currently stored in the task list.
     *
     * @param tasks Current task list.
     * @param storage Storage helper, unused because listing does not change saved data.
     * @param ui UI helper used to show the task list.
     */
    @Override
    public void execute(TaskList tasks, Storage storage, Ui ui) {
        ui.showTaskList(tasks);
    }
}
