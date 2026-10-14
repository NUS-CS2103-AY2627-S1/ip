package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Represents a user command that can be executed by bingus.Bingus.
 */
public abstract class Command {
    /**
     * Returns this command's category.
     *
     * @return command category.
     */
    public abstract CommandType getType();

    /**
     * Executes this command using the current application collaborators.
     *
     * @param tasks Current task list.
     * @param ui User interface used to format command output.
     * @param storage Persistent task storage.
     * @return response message after the command is executed.
     * @throws BingusException If the command cannot be completed.
     */
    public abstract String execute(TaskList tasks, Ui ui, Storage storage) throws BingusException;
}
