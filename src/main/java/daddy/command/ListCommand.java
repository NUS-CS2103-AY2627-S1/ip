package daddy.command;

import daddy.storage.Storage;
import daddy.task.TaskList;
import daddy.ui.Ui;

/**
 * Displays every task in the current task list.
 */
public class ListCommand extends Command {
    /** Creates a command that displays all current tasks. */
    public ListCommand() {
    }

    /** Displays the task list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
