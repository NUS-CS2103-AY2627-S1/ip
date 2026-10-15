package clearblue.command;

import clearblue.ClearblueException;
import clearblue.storage.Storage;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Represents one user command: something a chatbot input line resolves to
 * once it has been parsed, ready to be carried out against the task list.
 */
public abstract class Command {
    /**
     * Carries out this command: reading or changing {@code tasks} and
     * reporting through {@code ui}, saving via {@code storage} if the task
     * list changed.
     *
     * @param tasks task list to read or modify
     * @param ui user interface to report through
     * @param storage storage to persist any changes through
     * @throws ClearblueException if the command's target is invalid, or a save fails
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ClearblueException;

    /**
     * Returns whether this command should end the chatbot's session.
     *
     * @return {@code true} for {@link ExitCommand}, {@code false} otherwise
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns whether {@link #undo} can reverse this command's effect.
     * Commands that change the task list override this to return
     * {@code true}; read-only commands leave the default {@code false}.
     *
     * @return {@code true} if this command supports being undone
     */
    public boolean isUndoable() {
        return false;
    }

    /**
     * Reverses this command's effect on {@code tasks}, reports it through
     * {@code ui}, and saves the reverted list via {@code storage}. Only
     * ever called on a command for which {@link #isUndoable()} is
     * {@code true}; the default implementation exists to make misuse fail
     * loudly rather than silently do nothing.
     *
     * @param tasks task list to revert
     * @param ui user interface to report through
     * @param storage storage to persist the reverted list through
     * @throws ClearblueException if saving fails
     */
    public void undo(TaskList tasks, Ui ui, Storage storage) throws ClearblueException {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot be undone");
    }

    /**
     * Resolves a user-provided task number to a zero-based index, checking
     * it against the current task list. Shared by commands that target one
     * existing task by number.
     *
     * @param tasks task list the index must fall within
     * @param taskNumber one-based task number to validate
     * @param action operation that will use the selected task, for the error message
     * @return zero-based task index
     * @throws ClearblueException if there are no tasks, or the number is out of range
     */
    protected static int requireValidIndex(TaskList tasks, int taskNumber, String action)
            throws ClearblueException {
        if (tasks.isEmpty()) {
            throw new ClearblueException("There are no tasks to " + action + ".");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            String validRange = tasks.size() == 1 ? "1" : "1 to " + tasks.size();
            throw new ClearblueException(
                    "Task " + taskNumber + " does not exist. Choose " + validRange + ".");
        }

        int taskIndex = taskNumber - 1;
        // Callers (e.g. DeleteCommand, MarkCommand) trust this index and pass
        // it straight to TaskList.get/remove without re-checking bounds; if
        // the checks above ever stopped guaranteeing this range, those calls
        // would fail with an IndexOutOfBoundsException instead of the
        // friendly ClearblueException this method exists to produce.
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "requireValidIndex must return a valid zero-based index";
        return taskIndex;
    }
}
