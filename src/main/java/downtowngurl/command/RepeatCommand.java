package downtowngurl.command;

import downtowngurl.exception.DowntownGurlException;
import downtowngurl.storage.Storage;
import downtowngurl.task.Deadline;
import downtowngurl.task.Event;
import downtowngurl.task.RecurrenceFrequency;
import downtowngurl.task.Task;
import downtowngurl.task.TaskList;
import downtowngurl.ui.Ui;

/**
 * Represents the command that makes a dated task repeat weekly.
 */
public class RepeatCommand extends Command {
    private static final String NON_DATED_TASK_MESSAGE = "Only deadlines and events can repeat for now.";

    private final int taskIndex;
    private final RecurrenceFrequency recurrenceFrequency;

    /**
     * Creates a command for making the selected task repeat.
     *
     * @param taskIndex Zero-based index of the task to update.
     * @param recurrenceFrequency Frequency to assign to the task.
     */
    public RepeatCommand(int taskIndex, RecurrenceFrequency recurrenceFrequency) {
        this.taskIndex = taskIndex;
        this.recurrenceFrequency = recurrenceFrequency;
    }

    /**
     * Sets recurrence on the selected dated task and saves the updated task list.
     *
     * @param tasks Current task list.
     * @param storage Storage helper used to save task changes.
     * @param ui UI helper used to show command results.
     * @throws DowntownGurlException If the task cannot repeat or the change cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Storage storage, Ui ui) throws DowntownGurlException {
        requireValidTaskIndex(tasks, this.taskIndex);
        Task task = tasks.get(this.taskIndex);
        if (!(task instanceof Deadline || task instanceof Event)) {
            throw new DowntownGurlException(NON_DATED_TASK_MESSAGE);
        }

        RecurrenceFrequency previousFrequency = task.getRecurrenceFrequency();
        task.setRecurrenceFrequency(this.recurrenceFrequency);
        assert task.isRecurring() : "Repeating a task should set a recurrence frequency.";
        try {
            storage.saveTasks(tasks);
        } catch (DowntownGurlException e) {
            restoreRecurrence(task, previousFrequency);
            throw e;
        }
        ui.showUpdatedTask("Kays, I've made this task repeat weekly!", task);
    }
}
