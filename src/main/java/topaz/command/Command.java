package topaz.command;

import topaz.TopazException;
import topaz.storage.Storage;
import topaz.task.TaskList;
import topaz.ui.Ui;

/**
 * Represents an operation requested by the user.
 */
public abstract class Command {
    /**
     * Executes this command using the application's current collaborators.
     *
     * @param tasks the current task list
     * @param ui the user interface for displaying results
     * @param storage the storage used to persist changes
     * @throws TopazException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws TopazException;

    /**
     * Returns whether this command ends the application.
     *
     * @return true if this command ends the application
     */
    public boolean isExit() {
        return false;
    }
}
