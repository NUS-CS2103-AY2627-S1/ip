package lizzy.command;

import lizzy.exception.LizzyException;
import lizzy.storage.Storage;
import lizzy.task.TaskList;
import lizzy.ui.Ui;

/**
 * Represents one executable user request in Lizzy's command language.
 */
public abstract class Command {
    /**
     * Creates a command.
     */
    public Command() {
    }

    /**
     * Performs this command's operation.
     *
     * @param tasks the current task list
     * @param ui the component used for user interaction
     * @param storage the component used to persist task changes
     * @throws LizzyException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws LizzyException;

    /**
     * Returns whether executing this command ends the application session.
     *
     * @return {@code true} only for the exit command
     */
    public boolean isExit() {
        return false;
    }
}
