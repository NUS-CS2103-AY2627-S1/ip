package milo.command;

import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Represents an executable command entered by the user.
 */
public abstract class Command {

    /** Creates a command. */
    public Command() {
    }

    /**
     * Performs this command using Milo's task list, UI, and storage.
     *
     * @param tasks task list on which to operate.
     * @param ui console UI through which to present the result.
     * @param storage storage used to persist task changes.
     * @throws MiloException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws MiloException;

    /**
     * Returns whether executing this command should end the application.
     *
     * @return {@code true} if this command ends the application, otherwise {@code false}.
     */
    public boolean isExit() {
        return false;
    }
}
