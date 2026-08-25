package matthew.command;

import matthew.exception.MatthewException;
import matthew.storage.Storage;
import matthew.task.TaskList;
import matthew.ui.Ui;

/** Represents an operation that can be performed on the task list. */
public abstract class Command {

    /** Creates a command. */
    public Command() {
    }

    /**
     * Executes this command using the supplied application components.
     *
     * @param tasks Task list to read or modify.
     * @param ui User interface used to display the result.
     * @param storage Storage used to persist changes.
     * @throws MatthewException If the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws MatthewException;

    /**
     * Returns whether this command should terminate the application.
     *
     * @return {@code true} when the command should terminate the application.
     */
    public boolean isExit() {
        return false;
    }
}
