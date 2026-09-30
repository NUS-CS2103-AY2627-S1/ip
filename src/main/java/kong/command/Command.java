package kong.command;

import java.io.IOException;

import kong.exception.KongException;
import kong.storage.Storage;
import kong.task.TaskList;
import kong.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    /** Creates a command. */
    public Command() {
    }

    /**
     * Performs this command against the current application state.
     *
     * @param tasks current task list
     * @param ui console user interface
     * @param storage persistent task storage
     * @throws KongException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws KongException;

    /**
     * Indicates whether executing this command should end the application.
     *
     * @return {@code true} if this is an exit command; otherwise {@code false}
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the current tasks and translates storage errors into user-facing errors.
     *
     * @param tasks task list to save
     * @param storage destination storage
     * @throws KongException if the task list cannot be saved
     */
    protected void saveTasks(TaskList tasks, Storage storage) throws KongException {
        try {
            storage.saveTasks(tasks.asList());
        } catch (IOException e) {
            String detail = (e.getMessage() != null && !e.getMessage().isBlank())
                    ? ": " + e.getMessage()
                    : ".";
            throw new KongException("Unable to save tasks to disk" + detail);
        }
    }

    /**
     * Converts a one-based task number entered by the user to a zero-based index.
     *
     * @param arg task number entered by the user
     * @param commandName command name used in an error message
     * @return zero-based task index
     * @throws KongException if {@code arg} is not an integer
     */
    protected int parseTaskIndex(String arg, String commandName) throws KongException {
        try {
            return Integer.parseInt(arg.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new KongException(String.format("Invalid command. A %s command needs to be followed by a number.",
                    commandName));
        }
    }

    /**
     * Creates an error describing a task number outside the current list.
     *
     * @param tasks current task list
     * @return exception containing the current number of tasks
     */
    protected KongException invalidTaskNumberException(TaskList tasks) {
        return new KongException(String.format("This task number is invalid. You currently have %d tasks in your list.",
                tasks.size()));
    }
}
