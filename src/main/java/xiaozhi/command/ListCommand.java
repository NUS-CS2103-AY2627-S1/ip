package xiaozhi.command;

import xiaozhi.storage.Storage;
import xiaozhi.task.TaskList;
import xiaozhi.ui.Ui;

/**
 * Shows every task currently in the task list.
 */
public class ListCommand extends Command {

    /**
     * Prints the current task list through {@code ui}. Does not touch storage.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage, CommandHistory history) {
        ui.showList(tasks);
    }
}
