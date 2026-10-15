package beemo.command;

import beemo.BeemoException;
import beemo.storage.Storage;
import beemo.task.TaskList;
import beemo.ui.Ui;

/**
 * Represents an instruction that Beemo can execute.
 */
public abstract class Command {
    /**
     * Creates a command.
     */
    protected Command() {
    }

    /**
     * Performs the command using the application's task list, UI, and storage.
     *
     * @param tasks Task list on which the command operates.
     * @param ui User interface used to display the result.
     * @param storage Storage used to persist task changes.
     * @throws BeemoException If the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws BeemoException;

    /**
     * Returns whether this command should stop the application.
     *
     * @return True if the application should stop, otherwise false.
     */
    public boolean isExit() {
        return false;
    }
}
