package miku.command;

import java.util.Objects;

import miku.MikuException;
import miku.parser.DateTimeParser;
import miku.task.Deadline;
import miku.task.Event;
import miku.task.Task;
import miku.task.TaskList;
import miku.task.Todo;

/** Replaces the schedule of one existing deadline or event. */
public class RescheduleCommand extends TaskNumberCommand {
    private final DateTimeParser.ParsedDateTime dueDateTime;
    private final DateTimeParser.ParsedDateTime startDateTime;
    private final DateTimeParser.ParsedDateTime endDateTime;
    private final boolean isDeadlineReschedule;

    /** Creates a request to assign a deadline's new due date or time. */
    public RescheduleCommand(int taskNumber, DateTimeParser.ParsedDateTime dueDateTime) {
        super(taskNumber);
        this.dueDateTime = Objects.requireNonNull(dueDateTime);
        startDateTime = null;
        endDateTime = null;
        isDeadlineReschedule = true;
    }

    /** Creates a request to assign an event's new start and end dates or times. */
    public RescheduleCommand(int taskNumber, DateTimeParser.ParsedDateTime startDateTime,
            DateTimeParser.ParsedDateTime endDateTime) {
        super(taskNumber);
        dueDateTime = null;
        this.startDateTime = Objects.requireNonNull(startDateTime);
        this.endDateTime = Objects.requireNonNull(endDateTime);
        isDeadlineReschedule = false;
    }

    /** Replaces the selected schedule, persists it, and reports the revised task. */
    @Override
    public String execute(CommandContext commandContext) throws MikuException {
        TaskList tasks = commandContext.tasks();
        int taskIndex = getTaskIndex(tasks);
        Task originalTask = tasks.get(taskIndex);
        Task rescheduledTask = createRescheduledTask(originalTask);
        tasks.replace(taskIndex, rescheduledTask);
        try {
            saveTasks(commandContext);
        } catch (MikuException exception) {
            tasks.replace(taskIndex, originalTask);
            throw exception;
        }
        return commandContext.responseFormatter().formatTaskRescheduled(rescheduledTask);
    }

    /** Creates a replacement task with its original description and completion state. */
    private Task createRescheduledTask(Task task) throws MikuException {
        if (task instanceof Deadline deadline) {
            return rescheduleDeadline(deadline);
        }
        if (task instanceof Event event) {
            return rescheduleEvent(event);
        }
        assert task instanceof Todo : "Only supported task types may be rescheduled.";
        throw new MikuException("Only deadlines and events can be rescheduled!! \u266a");
    }

    /** Creates the replacement deadline after ensuring deadline syntax was used. */
    private Task rescheduleDeadline(Deadline deadline) throws MikuException {
        if (!isDeadlineReschedule) {
            throw new MikuException("A deadline needs its new due date using /by !! \u266b");
        }
        Deadline rescheduledDeadline = new Deadline(deadline.getDescription(), dueDateTime.dateTime(),
                dueDateTime.hasTime());
        return retainCompletionStatus(deadline, rescheduledDeadline);
    }

    /** Creates the replacement event after ensuring event syntax and range are valid. */
    private Task rescheduleEvent(Event event) throws MikuException {
        if (isDeadlineReschedule) {
            throw new MikuException("An event needs a new start using /from and an end using /to !! \u2728");
        }
        DateTimeParser.validateEventRange(startDateTime.dateTime(), endDateTime.dateTime());
        Event rescheduledEvent = new Event(event.getDescription(), startDateTime.dateTime(), startDateTime.hasTime(),
                endDateTime.dateTime(), endDateTime.hasTime());
        return retainCompletionStatus(event, rescheduledEvent);
    }

    /** Applies the original task's completed state to a replacement task. */
    private Task retainCompletionStatus(Task originalTask, Task replacementTask) {
        if (originalTask.isDone()) {
            replacementTask.markAsDone();
        }
        return replacementTask;
    }
}
