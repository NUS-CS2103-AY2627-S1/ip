package bob.command;

import java.io.IOException;

import bob.BobException;
import bob.Storage;
import bob.Ui;
import bob.task.TaskList;

/**
 * Represents a parsed user command.
 */
public abstract class Command {
    /**
     * Performs this command using the application's task list, UI, and storage.
     *
     * @param tasks task list to query or change
     * @param ui user interface used for command feedback
     * @param storage storage used to persist changes
     * @throws BobException if the command cannot be applied to the current task list
     * @throws IOException if a changed task list cannot be saved
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws BobException, IOException;

    /**
     * Returns whether this command should end the application.
     *
     * @return {@code true} only for an exit command
     */
    public boolean isExit() {
        return false;
    }
}
