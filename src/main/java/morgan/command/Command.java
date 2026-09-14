package morgan.command;

import morgan.exception.MorganException;
import morgan.storage.Storage;
import morgan.task.TaskList;
import morgan.ui.Ui;

/**
 * Represents a command in the application.
 */
public abstract class Command {
    /**
     * Executes the command using the given tasks, ui, and storage.
     *
     * @return True if this is an exit command, false otherwise.
     */
    public abstract boolean execute(TaskList tasks, Ui ui, Storage storage) throws MorganException;
}
