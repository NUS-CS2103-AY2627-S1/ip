package larry.command;

import larry.storage.Storage;
import larry.task.TaskList;
import larry.ui.Ui;

/**
 * Displays every task currently stored in the task list.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasks(tasks);
    }
}
