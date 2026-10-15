package jason.command;

import jason.exception.InvalidDeadlineException;
import jason.exception.InvalidEventException;
import jason.exception.InvalidIndexException;
import jason.exception.InvalidToDoException;
import jason.storage.Storage;
import jason.task.TaskList;
import jason.ui.Ui;

/**
 * A parsed user command that can be executed by the application.
 */
public abstract class Command {
    /**
     * Executes this command against the application state.
     *
     * @param tasks task list affected by the command.
     * @param ui user interface used by the command.
     * @param storage storage used to persist changes.
     * @throws InvalidToDoException if a todo command is invalid.
     * @throws InvalidDeadlineException if a deadline command is invalid.
     * @throws InvalidEventException if an event command is invalid.
     * @throws InvalidIndexException if a task index is invalid.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws InvalidToDoException, InvalidDeadlineException, InvalidEventException,
            InvalidIndexException;

    /**
     * Returns whether this command ends the application.
     *
     * @return true if this command exits the application; otherwise false.
     */
    public boolean isExit() {
        return false;
    }
}
