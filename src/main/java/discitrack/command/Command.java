package discitrack.command;

import java.io.IOException;

import discitrack.exception.DisciTrackException;
import discitrack.storage.Storage;
import discitrack.task.TaskList;
import discitrack.ui.Ui;

/**
 * Represents a user command that can be executed by DisciTrack.
 */
public abstract class Command {
    /**
     * Executes this command using the current task list, UI, and storage.
     *
     * @param tasks the task list to operate on.
     * @param ui the UI used to show command results.
     * @param storage the storage used to persist task changes.
     * @throws DisciTrackException if the command cannot be completed.
     * @throws IOException if saving task changes fails.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws DisciTrackException, IOException;

    /**
     * Returns whether this command should end the application.
     *
     * @return true if this command exits the application, otherwise false.
     */
    public boolean isExit() {
        return false;
    }
}
