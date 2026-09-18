package sandrone.command;

import sandrone.SandroneException;
import sandrone.storage.Storage;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/**
 * Represents one action that the chatbot can perform.
 */
public abstract class Command {
    /**
     * Performs this command using the application's collaborators.
     *
     * @param tasks current task list
     * @param ui user interface
     * @param storage task storage
     * @throws SandroneException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws SandroneException;

    /** Returns whether executing this command should end the application. */
    public boolean isExit() {
        return false;
    }
}
