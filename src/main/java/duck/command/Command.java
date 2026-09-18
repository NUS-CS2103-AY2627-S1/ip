package duck.command;

import duck.DuckException;
import duck.storage.Storage;
import duck.task.TaskList;
import duck.ui.Ui;

/**
 * Represents an executable user command.
 * Concrete commands encapsulate the behavior needed to respond to one command.
 */
public abstract class Command {
    /**
     * Creates a command.
     */
    public Command() {
    }

    /**
     * Executes this command using the application's collaborators.
     *
     * @param tasks Task list to query or update.
     * @param ui User interface used to display responses.
     * @param storage Persistent task storage.
     * @throws DuckException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws DuckException;

    /**
     * Returns whether this command should end the application.
     *
     * @return true only for an exit command
     */
    public boolean isExit() {
        return false;
    }
}
