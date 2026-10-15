package duncan.command;

import java.time.LocalDate;

import duncan.DuncanException;
import duncan.Storage;
import duncan.Ui;
import duncan.task.Deadline;
import duncan.task.Event;
import duncan.task.Task;
import duncan.task.TaskList;

/**
 * Moves the deadline or event at a given 0-based index to new dates typed by
 * the user: a new "by" date for a deadline, or new "from" and "to" dates for an
 * event. Whether the task is done is unchanged.
 */
public class RescheduleCommand extends Command {
    private final int taskIndex;

    /** The new due date if the task is a deadline, or null if none was given. */
    private final LocalDate by;

    /** The new start date if the task is an event, or null if none was given. */
    private final LocalDate from;

    /** The new end date if the task is an event, or null if none was given. */
    private final LocalDate to;

    /**
     * Creates a command that reschedules the task at the given index. Only the
     * dates that suit the task's type are used; the task's type is not known
     * until the command runs, so either kind of date may be null.
     *
     * @param taskIndex The 0-based index of the task to reschedule.
     * @param by The new due date for a deadline, or null.
     * @param from The new start date for an event, or null.
     * @param to The new end date for an event, or null.
     */
    public RescheduleCommand(int taskIndex, LocalDate by, LocalDate from, LocalDate to) {
        assert taskIndex >= 0;
        assert (from == null) == (to == null);
        this.taskIndex = taskIndex;
        this.by = by;
        this.from = from;
        this.to = to;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DuncanException {
        checkTaskIndex(tasks, taskIndex);
        Task task = tasks.get(taskIndex);
        String oldTask = task.toString();

        if (task instanceof Deadline deadline) {
            if (by == null) {
                throw new DuncanException(MESSAGE_MISSING_BY);
            }
            deadline.reschedule(by);
        } else if (task instanceof Event event) {
            if (from == null) {
                throw new DuncanException(MESSAGE_MISSING_FROM_TO);
            }
            event.reschedule(from, to);
        } else {
            throw new DuncanException(MESSAGE_CANNOT_RESCHEDULE);
        }

        ui.showTaskRescheduled(oldTask, task);
        saveTasks(tasks, ui, storage);
    }
}
