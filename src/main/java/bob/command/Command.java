package bob.command;

import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Represents an executable command in the application.
 */
public abstract class Command {

    /**
     * Constructs a Command.
     */
    protected Command() {
    }

    /**
     * Executes the command with the given task list, user interface, and storage.
     *
     * @param tasks   the list of tasks
     * @param ui      the user interface handler
     * @param storage the storage handler
     * @throws BobException if an error occurs during execution
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage<TaskList> storage) throws BobException;

    /**
     * Asserts that none of the core execution dependencies are null.
     *
     * @param tasks   the list of tasks
     * @param ui      the user interface handler
     * @param storage the storage handler
     */
    protected void assertExecutionDependencies(TaskList tasks, Ui ui, Storage<TaskList> storage) {
        assert tasks != null : "TaskList should not be null during command execution";
        assert ui != null : "Ui should not be null during command execution";
        assert storage != null : "TaskStorage should not be null during command execution";
    }

    /**
     * Indicates whether this command causes the application to exit.
     *
     * @return true if the command causes an exit, false otherwise
     */
    public boolean isExit() {
        return false;
    }
}
