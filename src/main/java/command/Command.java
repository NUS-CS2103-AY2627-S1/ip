package puyo.command;

import puyo.PuyoException;
import puyo.storage.Storage;
import puyo.task.TaskList;
import puyo.ui.Ui;

/**
 * Represents an abstract command that can be executed by the application.
 */
public abstract class Command {

    /**
     * Executes the command using the provided task list, user interface, and storage.
     *
     * @param tasks The list of tasks.
     * @param ui The user interface for interaction.
     * @param storage The storage handler.
     * @throws PuyoException If an error occurs during command execution.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws PuyoException;

    /**
     * Indicates whether this command signals the application to exit.
     *
     * @return {@code true} if the command terminates the app, {@code false} otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
