package arin.command;

import java.io.IOException;
import java.util.List;

import arin.exception.ArinException;
import arin.storage.Storage;
import arin.task.Task;
import arin.ui.Ui;

/**
 * Represents an instruction that can be executed by Arin.
 */
public abstract class Command {
    /**
     * Performs this command using Arin's current collaborators.
     *
     * @param tasks Current tasks.
     * @param ui User interface for feedback.
     * @param storage Persistent task storage.
     * @throws ArinException If command execution cannot be completed.
     * @throws IOException If saving task data fails.
     */
    public abstract void execute(List<Task> tasks, Ui ui, Storage storage)
            throws ArinException, IOException;

    /**
     * Returns whether this command ends Arin's main loop.
     *
     * @return {@code false} for ordinary commands.
     */
    public boolean isExit() {
        return false;
    }
}
