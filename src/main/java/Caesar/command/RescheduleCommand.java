package caesar.command;

import caesar.exception.CaesarException;
import caesar.storage.Storage;
import caesar.task.Deadline;
import caesar.task.Event;
import caesar.task.Task;
import caesar.task.TaskList;
import caesar.ui.Ui;

/** Reschedules one deadline or event and persists the updated task list. */
public class RescheduleCommand extends Command {
    /** One-based task number selected by the user. */
    private final int taskNumber;
    /** The new deadline or event start date. */
    private final String newStart;
    /** The new event end date, or {@code null} for a deadline. */
    private final String newEnd;

    /** Creates a reschedule command with one new date for a deadline. */
    public RescheduleCommand(int taskNumber, String newStart) {
        this(taskNumber, newStart, null);
    }

    /** Creates a reschedule command with new start and end dates for an event. */
    public RescheduleCommand(int taskNumber, String newStart, String newEnd) {
        this.taskNumber = taskNumber;
        this.newStart = newStart;
        this.newEnd = newEnd;
    }

    /** Reschedules the selected task, rolls back on save failure, and reports success. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws CaesarException {
        Task task = tasks.get(taskNumber);
        if (task instanceof Deadline deadline) {
            rescheduleDeadline(deadline, tasks, storage, ui);
            return;
        }
        if (task instanceof Event event) {
            rescheduleEvent(event, tasks, storage, ui);
            return;
        }
        throw new CaesarException("Only deadline and event tasks can be rescheduled.");
    }

    /** Reschedules a deadline and restores its original date if saving fails. */
    private void rescheduleDeadline(Deadline deadline, TaskList tasks, Storage storage, Ui ui)
            throws CaesarException {
        if (newEnd != null) {
            throw new CaesarException("Deadlines require one date: reschedule <number> <date>.");
        }

        String originalDeadline = deadline.getBy();
        deadline.reschedule(newStart);
        try {
            storage.save(tasks);
        } catch (CaesarException exception) {
            deadline.reschedule(originalDeadline);
            throw exception;
        }
        ui.showTaskRescheduled(deadline);
    }

    /** Reschedules an event and restores its original dates if saving fails. */
    private void rescheduleEvent(Event event, TaskList tasks, Storage storage, Ui ui)
            throws CaesarException {
        if (newEnd == null) {
            throw new CaesarException("Events require two dates: reschedule <number> <start> <end>.");
        }

        String originalStart = event.getStart();
        String originalEnd = event.getEnd();
        event.reschedule(newStart, newEnd);
        try {
            storage.save(tasks);
        } catch (CaesarException exception) {
            event.reschedule(originalStart, originalEnd);
            throw exception;
        }
        ui.showTaskRescheduled(event);
    }
}
