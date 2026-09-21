package ted.command;

import ted.Storage;
import ted.Ui;
import ted.task.TaskList;

/**
 * Shows every stored task as a numbered list.
 */
public class ListCommand extends Command {
    /** Creates a command that shows the task list. */
    public ListCommand() {
    }

    /** Shows the whole task list. Nothing is changed, so nothing is saved. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasks(tasks);
    }
}
