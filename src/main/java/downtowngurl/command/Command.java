package downtowngurl.command;

import downtowngurl.exception.DowntownGurlException;
import downtowngurl.storage.Storage;
import downtowngurl.task.RecurrenceFrequency;
import downtowngurl.task.Task;
import downtowngurl.task.TaskList;
import downtowngurl.ui.Ui;

/**
 * Represents a user command after it has been parsed.
 */
public abstract class Command {
    private static final String INVALID_TASK_NUMBER_MESSAGE = "Maybe you could try using a task number from your list."
            + "\nGirl you need some sleep... I don't understand whatchu talking about.";

    /**
     * Executes this command.
     *
     * @param tasks Current task list.
     * @param storage Storage helper used to save task changes.
     * @param ui UI helper used to show command results.
     * @throws DowntownGurlException If the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Storage storage, Ui ui) throws DowntownGurlException;

    /**
     * Returns whether this command should exit the application after execution.
     *
     * @return true if the application should exit, false otherwise.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Restores a task's done status after a failed save.
     *
     * @param task Task to restore.
     * @param wasDone Previous done status.
     */
    protected void restoreTaskStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        assert task.isDone() == wasDone : "Task status should be restored after a failed save.";
    }

    /**
     * Restores a task's recurrence after a failed save.
     *
     * @param task Task to restore.
     * @param previousFrequency Previous recurrence frequency.
     */
    protected void restoreRecurrence(Task task, RecurrenceFrequency previousFrequency) {
        if (previousFrequency == null) {
            task.clearRecurrence();
        } else {
            task.setRecurrenceFrequency(previousFrequency);
        }
        assert task.getRecurrenceFrequency() == previousFrequency
                : "Task recurrence should be restored after a failed save.";
    }

    /**
     * Checks that a parsed task index exists in the current task list.
     *
     * @param tasks Current task list.
     * @param taskIndex Zero-based task index to check.
     * @throws DowntownGurlException If the task index does not exist.
     */
    protected void requireValidTaskIndex(TaskList tasks, int taskIndex) throws DowntownGurlException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new DowntownGurlException(INVALID_TASK_NUMBER_MESSAGE);
        }
        assert taskIndex >= 0 && taskIndex < tasks.size() : "Validated task index should be in range.";
    }
}
