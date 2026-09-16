package downtowngurl.command;

import downtowngurl.exception.DowntownGurlException;
import downtowngurl.storage.Storage;
import downtowngurl.task.RecurrenceFrequency;
import downtowngurl.task.Task;
import downtowngurl.task.TaskList;
import downtowngurl.ui.Ui;

/**
 * Represents the command that removes recurrence from a task.
 */
public class UnrepeatCommand extends Command {
    private static final String NOT_RECURRING_MESSAGE = "That task is not recurring yet.";

    private final int taskIndex;

    /**
     * Creates a command for removing recurrence from the selected task.
     *
     * @param taskIndex Zero-based index of the task to update.
     */
    public UnrepeatCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Clears recurrence from the selected task and saves the updated task list.
     *
     * @param tasks Current task list.
     * @param storage Storage helper used to save task changes.
     * @param ui UI helper used to show command results.
     * @throws DowntownGurlException If the task is not recurring or the change cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Storage storage, Ui ui) throws DowntownGurlException {
        requireValidTaskIndex(tasks, this.taskIndex);
        Task task = tasks.get(this.taskIndex);
        if (!task.isRecurring()) {
            throw new DowntownGurlException(NOT_RECURRING_MESSAGE);
        }

        RecurrenceFrequency previousFrequency = task.getRecurrenceFrequency();
        task.clearRecurrence();
        assert !task.isRecurring() : "Unrepeating a task should clear its recurrence frequency.";
        try {
            storage.saveTasks(tasks);
        } catch (DowntownGurlException e) {
            restoreRecurrence(task, previousFrequency);
            throw e;
        }
        ui.showUpdatedTask("Sure, I've stopped this task from repeating.", task);
    }
}
