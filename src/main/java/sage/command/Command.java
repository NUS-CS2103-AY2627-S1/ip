package sage.command;

import sage.core.TaskList;
import sage.exception.SageException;
import sage.storage.Storage;
import sage.ui.Ui;

/**
 * Represents an action that can be executed against the task list.
 */
public abstract class Command {
    /**
     * Initializes a command with no shared state.
     */
    public Command() {
    }

    /**
     * Executes this command.
     *
     * @param tasks the task list available to the command.
     * @param ui the user interface used to display output.
     * @param storage the storage system available to the command.
     * @throws SageException if the command cannot run with the current data.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws SageException;

    /**
     * Indicates whether this command exits the application.
     *
     * @return true if the command should terminate the loop; false otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
