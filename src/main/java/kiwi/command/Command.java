package kiwi.command;

import kiwi.KiwiException;
import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.TaskList;

/**
 * A user command that can be executed against the task list, UI, and storage.
 * Subclasses implement the specific behaviour for each command word.
 */
public abstract class Command {

    /**
     * Runs this command.
     *
     * @param tasks the in-memory task list.
     * @param ui user interface for messages.
     * @param storage persistence for saving after changes.
     * @throws KiwiException If the command cannot be completed (e.g. bad index).
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws KiwiException;

    /**
     * Ensures {@code index} refers to an existing task.
     *
     * @param tasks the task list to check against.
     * @param index 0-based index from the user command.
     * @throws KiwiException If the index is out of range.
     */
    protected void requireValidIndex(TaskList tasks, int index) throws KiwiException {
        if (!tasks.isValidIndex(index)) {
            throw new KiwiException("There is no task number " + toDisplayNumber(index)
                    + " in your list.");
        }
    }

    /**
     * Converts a 0-based list index to the 1-based number shown to the user.
     *
     * @param zeroBasedIndex position in the task list.
     * @return the corresponding user-visible task number.
     */
    protected static int toDisplayNumber(int zeroBasedIndex) {
        return zeroBasedIndex + 1;
    }

    /**
     * Saves the task list, running {@code rollback} if writing fails so memory matches disk.
     *
     * @param tasks current task list.
     * @param storage persistence.
     * @param rollback undoes the in-memory change that has not been saved.
     * @throws KiwiException If the list could not be written.
     */
    protected void saveOrRollback(TaskList tasks, Storage storage, Runnable rollback) throws KiwiException {
        try {
            storage.save(tasks.getTasks());
        } catch (KiwiException e) {
            rollback.run();
            throw e;
        }
    }

    /**
     * Returns whether this command should end the chatbot loop.
     *
     * @return {@code true} only for exit; default is {@code false}.
     */
    public boolean isExit() {
        return false;
    }
}
