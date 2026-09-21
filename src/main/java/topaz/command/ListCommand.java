package topaz.command;

import topaz.storage.Storage;
import topaz.task.TaskList;
import topaz.ui.Ui;

/**
 * Displays the current task list.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
