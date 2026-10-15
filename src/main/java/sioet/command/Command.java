package sioet.command;

import sioet.SioetException;
import sioet.storage.Storage;
import sioet.task.TaskList;
import sioet.ui.Ui;

/**
 * Represents a command that can be executed by sioet.ui.Sioet.
 */
public abstract class Command {

    /**
     * Executes this command.
     *
     * @param tasks   the task list
     * @param ui      the user interface
     * @param storage the task storage
     * @throws SioetException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws SioetException;

    /**
     * Returns whether this command should exit sioet.ui.Sioet.
     *
     * @return true if sioet.ui.Sioet should exit, false otherwise
     */
    public boolean isExit() {
        return false;
    }
}
