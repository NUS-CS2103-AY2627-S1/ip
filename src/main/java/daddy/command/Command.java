package daddy.command;

import daddy.exception.DaddyException;
import daddy.storage.Storage;
import daddy.task.TaskList;
import daddy.ui.Ui;

/**
 * Represents one action that can be performed in response to a user command.
 */
public abstract class Command {
    /** Creates a command for a user-requested application action. */
    protected Command() {
    }

    /**
     * Performs this command using the application's collaborating objects.
     *
     * @param tasks the current task list
     * @param ui the user interface
     * @param storage the persistent task storage
     * @throws DaddyException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws DaddyException;

    /**
     * Returns whether this command ends the application.
     *
     * @return {@code false} unless overridden by an exit command
     */
    public boolean isExit() {
        return false;
    }
}
