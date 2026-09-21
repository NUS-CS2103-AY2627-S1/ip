package peter.command;

import peter.storage.Storage;
import peter.task.TaskList;
import peter.ui.Ui;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {

    /**
     * {@inheritDoc}
     *
     * <p>Reads the task list without changing it, so nothing is saved. An
     * empty list prints only the heading.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
