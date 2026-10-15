package pip.command;

import pip.storage.Storage;
import pip.task.TaskList;
import pip.ui.Ui;


/**
 * Represents an executable user command.
 *
 * <p>Each concrete subclass encapsulates one user action (e.g. adding a
 * task, deleting a task) and defines how it mutates the task list, what
 * it reports to the user, and whether it should end the program.
 */
public abstract class Command {
    /** Initializes the base of an executable command. */
    protected Command() {
    }

    /**
     * Executes this command against the given task list, reporting results
     * through the given UI and persisting changes through the given storage.
     *
     * @param tasks The task list this command operates on.
     * @param ui The UI used to report the outcome to the user.
     * @param storage The storage used to persist any changes made.
     * @throws Exception If the command's arguments are invalid or an I/O error occurs.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws Exception;

    /**
     * Returns whether this command should terminate the program's main loop.
     *
     * @return {@code true} if the program should exit after this command, {@code false} otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
