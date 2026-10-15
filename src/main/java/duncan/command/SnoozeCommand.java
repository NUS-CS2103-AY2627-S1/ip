package duncan.command;

import java.time.DateTimeException;

import duncan.DuncanException;
import duncan.Storage;
import duncan.Ui;
import duncan.task.Deadline;
import duncan.task.Event;
import duncan.task.Task;
import duncan.task.TaskList;

/**
 * Pushes the dates of the deadline or event at a given 0-based index back by a
 * number of days: a deadline's due date, or both of an event's dates. Whether
 * the task is done is unchanged.
 */
public class SnoozeCommand extends Command {
    private final int taskIndex;
    private final int days;

    /**
     * Creates a command that snoozes the task at the given index.
     *
     * @param taskIndex The 0-based index of the task to snooze.
     * @param days How many days later the task's dates should be, at least 1.
     */
    public SnoozeCommand(int taskIndex, int days) {
        assert taskIndex >= 0;
        assert days >= 1;
        this.taskIndex = taskIndex;
        this.days = days;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DuncanException {
        checkTaskIndex(tasks, taskIndex);
        Task task = tasks.get(taskIndex);
        String oldTask = task.toString();

        try {
            if (task instanceof Deadline deadline) {
                deadline.snooze(days);
            } else if (task instanceof Event event) {
                event.snooze(days);
            } else {
                throw new DuncanException(MESSAGE_CANNOT_RESCHEDULE);
            }
        } catch (DateTimeException e) {
            // The new date would be past the latest date LocalDate supports; the task is left unchanged.
            throw new DuncanException(MESSAGE_INVALID_DAYS);
        }

        ui.showTaskSnoozed(oldTask, task, days);
        saveTasks(tasks, ui, storage);
    }
}
