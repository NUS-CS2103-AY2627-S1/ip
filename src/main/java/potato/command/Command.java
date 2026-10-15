package potato.command;

import potato.exception.PotatoException;
import potato.storage.Storage;
import potato.tasklist.TaskList;
import potato.ui.Ui;

/**
 * Represents an executable command issued by the user.
 */
public abstract class Command {

    /**
     * Executes the command using the given task list, UI context, and file storage.
     *
     * @param tasks Task list modified by command execution.
     * @param ui User interface for output generation.
     * @param storage Data persistence layer for saving changes.
     * @throws PotatoException If an error occurs during command execution.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws PotatoException;

    /**
     * Indicates whether execution of this command terminates the application.
     *
     * @return {@code true} if the program should exit after this command; {@code false} otherwise.
     */
    public boolean isExit() {
        return false;
    }
}