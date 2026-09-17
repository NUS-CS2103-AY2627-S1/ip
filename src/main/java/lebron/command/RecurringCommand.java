package lebron.command;

import java.time.LocalDateTime;

import lebron.exception.LeBronException;
import lebron.task.RecurrenceInterval;
import lebron.task.RecurringTask;
import lebron.task.Task;
import lebron.task.TaskList;
import lebron.ui.Ui;

/**
 * Represents the "recur" command, which adds a RecurringTask to the TaskList.
 */
public class RecurringCommand implements Command {
    private final String description;
    private final LocalDateTime nextDue;
    private final RecurrenceInterval interval;

    /**
     * Constructs a RecurringCommand with the given description, next due date/time, and interval.
     *
     * @param description The RecurringTask's description.
     * @param nextDue The date/time the RecurringTask's first occurrence is due.
     * @param interval How often the RecurringTask recurs.
     */
    public RecurringCommand(String description, LocalDateTime nextDue, RecurrenceInterval interval) {
        this.description = description;
        this.nextDue = nextDue;
        this.interval = interval;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws LeBronException {
        Task task = new RecurringTask(description, nextDue, interval);
        taskList.addTask(task);
        return ui.showRecurringAdded(task, taskList.size());
    }
}
