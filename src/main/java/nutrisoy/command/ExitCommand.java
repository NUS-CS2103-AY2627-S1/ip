package nutrisoy.command;

import nutrisoy.exception.DukeException;
import nutrisoy.task.TaskList;
import nutrisoy.ui.Ui;

/**
 * Represents a command that exits the application.
 */
public class ExitCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public boolean changesTasks() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) throws DukeException {
        assert tasks != null && ui != null : "Command collaborators must not be null";
        ui.showGoodbye();
    }

    /**
     * Indicates that this command exits the application.
     *
     * @return {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
