package sage.command;

import java.io.IOException;

import sage.exception.SageException;
import sage.model.TaskList;
import sage.storage.Storage;
import sage.ui.Ui;

/** Represents one executable user command. */
public abstract class Command {
    /**
     * Creates a command.
     */
    protected Command() {
    }

    /** Executes this command using the application's collaborators.
     *
     * @param tasks current task list
     * @param ui interface used to display responses
     * @param storage persistent task storage
     * @throws IOException when task data cannot be saved
     * @throws SageException when command input is invalid
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws IOException, SageException;

    /** Returns whether executing this command should end the application.
     *
     * @return whether this command exits the application
     */
    public boolean isExit() {
        return false;
    }
}
