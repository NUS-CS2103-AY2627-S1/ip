package duke.command;

import duke.exception.DukeException;
import duke.storage.Storage;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Represents an abstract executable command in the Duke application.
 */
public abstract class Command {
    /**
     * Constructs a Command.
     */
    public Command() {
    }

    /**
     * Executes the command with the provided TaskList, Ui, and Storage.
     *
     * @param tasks   The task list on which to execute the command.
     * @param ui      The user interface for displaying output.
     * @param storage The storage handler for saving tasks.
     * @throws DukeException If an error occurs during execution.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException;

    /**
     * Indicates whether this command is an exit command.
     *
     * @return True if this command terminates the application, false otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
