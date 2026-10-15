package nutrisoy.command;

import nutrisoy.exception.DukeException;
import nutrisoy.task.TaskList;
import nutrisoy.ui.Ui;

/**
 * Displays all tasks in the task list.
 */
public class ListCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public boolean changesTasks() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) throws DukeException {
        assert tasks != null && ui != null : "Command collaborators must not be null";
        ui.showTaskList(tasks);
    }
}
