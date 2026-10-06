package caesar.command;

import caesar.exception.CaesarException;
import caesar.storage.Storage;
import caesar.task.TaskList;
import caesar.ui.Ui;

/**
 * An executable user command.
 *
 * <p>Concrete command classes receive the application collaborators they need
 * at execution time. This keeps command parsing separate from command effects.</p>
 */
public abstract class Command {
    /** Executes this command using the current application state. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws CaesarException;

    /** Returns whether this command should stop the application loop. */
    public boolean isExit() {
        return false;
    }
}
