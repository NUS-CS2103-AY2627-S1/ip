package glennon.command;

import glennon.Storage;
import glennon.Ui;
import glennon.exception.GlennonException;
import glennon.task.TaskList;

/**
 * Represents one parsed user instruction that can act on the application.
 */
public abstract class Command {
    /**
     * Creates a command for Glennon to execute.
     */
    public Command() {
    }

    /**
     * Executes this command using the application's collaborators.
     *
     * @param missions missions in the current session.
     * @param ui interface used to display the result.
     * @param storage storage used when persistent state changes.
     * @throws GlennonException if the command cannot be completed.
     */
    public abstract void execute(TaskList missions, Ui ui, Storage storage)
            throws GlennonException;

    /**
     * Reports whether executing this command should end the application.
     *
     * @return false for commands that do not exit.
     */
    public boolean isExit() {
        return false;
    }
}
