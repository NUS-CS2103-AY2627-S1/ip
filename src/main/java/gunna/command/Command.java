package gunna.command;

import gunna.DukeException;
import gunna.Storage;
import gunna.TaskList;
import gunna.Ui;

/**
 * Represents an executable command in the Gunna application.
 * Commands encapsulate the logic for processing user input.
 */
public abstract class Command {

    /**
     * Executes the command.
     *
     * @param tasks The task list to operate on.
     * @param ui The UI to display messages to the user.
     * @param storage The storage to save/load tasks.
     * @return The response message to display to the user.
     * @throws DukeException If an error occurs during command execution.
     */
    public abstract String execute(TaskList tasks, Ui ui, Storage storage) throws DukeException;

    /**
     * Returns whether this command should cause the application to exit.
     *
     * @return true if the application should exit after this command, false otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
