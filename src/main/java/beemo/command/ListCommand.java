package beemo.command;

import beemo.storage.Storage;
import beemo.task.TaskList;
import beemo.ui.Ui;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that displays the task list.
     */
    public ListCommand() {
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.asList());
    }
}
