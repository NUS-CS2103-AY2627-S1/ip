package nob.command;

import nob.exception.NobException;
import nob.storage.Storage;
import nob.task.TaskList;
import nob.ui.Ui;

/**
 * Represents one action that Nob can perform for a user command.
 */
public abstract class Command {
    /**
     * Performs this command using the application's collaborators.
     *
     * @param tasks The user's task list.
     * @param ui The console user interface.
     * @param storage Persistent task storage.
     * @throws NobException If the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws NobException;
}
