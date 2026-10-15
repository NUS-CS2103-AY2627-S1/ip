package zen.command;

import zen.ZenException;
import zen.storage.Storage;
import zen.task.TaskList;
import zen.ui.Ui;

/**
 * Represents one user command that can operate on the application's state.
 */
public abstract class Command {
    /**
     * Executes this command's actions, mutates the list,
     * producing console outputs and persisting changes if needed.
     *
     * @param tasks task list to operate on
     * @param ui console interface used for output
     * @param storage persistence service for task changes
     * @throws ZenException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ZenException;

    /**
     * Returns whether executing this command ends the application loop.
     *
     * @return true if the command requests application exit
     */
    public boolean isExit() {
        return false;
    }
}
