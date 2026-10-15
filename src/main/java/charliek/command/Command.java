package charliek.command;

import java.util.OptionalInt;

import charliek.CharlieK;
import charliek.exception.CharlieKException;
import charliek.exception.TaskStorageException;
import charliek.model.TaskList;
import charliek.storage.Storage;
import charliek.ui.Ui;

/**
 * Represents an executable command in the application.
 *
 * <p>Concrete commands receive the application collaborators they need so
 * that command-specific behavior can be moved out of {@link CharlieK}.</p>
 */
public abstract class Command {
    /**
     * The first task number shown to users.
     */
    private static final int FIRST_TASK_NUMBER = 1;

    /**
     * Creates a command base instance for use by a concrete command.
     */
    protected Command() {
    }

    /**
     * Executes this command.
     *
     * @throws CharlieKException if the command cannot complete normally.
     */
    public abstract void execute() throws CharlieKException;

    /**
     * Indicates whether this command ends the application session.
     *
     * @return {@code true} when the command requests application exit.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the current tasks and restores the prior state if saving fails.
     *
     * <p>Commands mutate the shared task list before persistence so their
     * success messages can reflect the new state. A rollback keeps memory and
     * disk consistent when persistence cannot complete.</p>
     *
     * @param storage the storage service used to persist the task list.
     * @param tasks the task list to save.
     * @param rollback restores the task list state from before the mutation.
     * @throws TaskStorageException if the task list cannot be saved.
     */
    protected final void saveTasksOrRollback(Storage storage, TaskList tasks, Runnable rollback)
            throws TaskStorageException {
        try {
            storage.save(tasks.toList());
        } catch (TaskStorageException | RuntimeException exception) {
            rollback.run();
            throw exception;
        }
    }

    /**
     * Converts a user-facing task number into a zero-based list index.
     *
     * @param taskNumberText the one-based task number entered by the user.
     * @param tasks the task list used to validate the number.
     * @param ui the UI used to report invalid input.
     * @return the zero-based task index, or an empty optional when the number is invalid.
     */
    protected final OptionalInt parseTaskIndex(String taskNumberText, TaskList tasks, Ui ui) {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < FIRST_TASK_NUMBER || taskNumber > tasks.size()) {
                ui.showTaskDoesNotExist();
                return OptionalInt.empty();
            }
            return OptionalInt.of(taskNumber - FIRST_TASK_NUMBER);
        } catch (NumberFormatException exception) {
            ui.showInvalidTaskNumber();
            return OptionalInt.empty();
        }
    }
}
