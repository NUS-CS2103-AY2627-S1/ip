package thomas.command;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Represents one command that can be executed by Thomas.
 */
public abstract class Command {
    /**
     * Executes this command.
     *
     * @param tasks Task list to change or inspect.
     * @param ui User interface used for output.
     * @param storage Storage used to save changes.
     * @throws ThomasException If the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ThomasException;

    /**
     * Returns whether this command ends the application.
     *
     * @return True if the application should exit.
     */
    public boolean isExit() {
        return false;
    }
}
