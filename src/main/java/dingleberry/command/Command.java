package dingleberry.command;

import java.io.IOException;

import dingleberry.exception.DingleberryException;
import dingleberry.model.TaskList;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/**
 * Represents a single user command, already validated and ready to run
 * against the app's collaborators. Produced by
 * {@link dingleberry.parser.Parser#parse}.
 */
public abstract class Command {
    /**
     * Carries out this command's effect on the task list, UI, and storage.
     *
     * @param tasks the in-memory task list to mutate or read
     * @param ui the UI used to print success or error messages
     * @param storage the persistence layer used to save modified task data
     * @throws DingleberryException if command execution fails validation or
     *     business rules.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws DingleberryException;

    /**
      * Returns whether executing this command should end the program's main
      * loop.
     *
      * @return true if the app should stop reading user input after this
      *     command.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Validates a one-based task number and returns its zero-based index.
     * Bounds are checked during execution because the task list may change.
     *
     * @param taskNumber the one-based task number supplied by the user.
     * @param tasks the task list used to validate the number.
     * @return the validated zero-based index.
     * @throws DingleberryException if the task number is outside the list.
     */
    protected final int requireTaskIndex(final int taskNumber,
            final TaskList tasks)
            throws DingleberryException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new DingleberryException(
                    "That task number is not in the list.");
        }
        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A validated task number must map to a valid list index.";
        return taskIndex;
    }

    /**
        * Persists the task list, reporting any I/O failure through the UI.
     *
     * @param tasks the task list to save
     * @param storage the storage object responsible for writing data
     * @param ui the UI used to display save errors
     */
    protected void saveTasks(final TaskList tasks, final Storage storage,
                             final Ui ui) {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            ui.showSavingError(e.getMessage());
        } catch (RuntimeException e) {
            String message = e.getMessage() == null
                    ? e.getClass().getSimpleName() : e.getMessage();
            ui.showSavingError("Unexpected storage error: " + message);
        }
    }
}
