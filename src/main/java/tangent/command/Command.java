package tangent.command;

import tangent.exception.TangentException;
import tangent.storage.Storage;
import tangent.task.TaskList;
import tangent.ui.Ui;

/** Represents an action that can be performed in response to a user command. */
public abstract class Command {
    /**
     * Executes the command using the supplied task list, user interface, and storage.
     *
     * @param tasks The list of tasks currently held in memory.
     * @param ui User interface object that handles all console responses.
     * @param storage Storage object that handles all interactions with the data file.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws TangentException;

    /**
     * Returns whether executing this command should end the application.
     * Only {@link ExitCommand#isExit()} should return true.
     */
    public boolean isExit() {
        return false;
    }
}
