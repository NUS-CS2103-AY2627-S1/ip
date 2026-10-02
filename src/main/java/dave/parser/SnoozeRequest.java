package dave.parser;

import java.time.temporal.ChronoUnit;

import dave.exception.DaveCommandException;
import dave.task.Deadline;
import dave.task.Event;
import dave.task.Task;

/**
 * Represents a parsed request to snooze or reschedule a task in the task list.
 */
public class SnoozeRequest {

    /** 0-based index of the task to snooze. */
    private final int index;
    /** Amount of time units for relative snooze. */
    private final long relativeAmount;
    /** Unit of time for relative snooze. */
    private final ChronoUnit relativeUnit;
    /** Target date-time for deadline rescheduling. */
    private final ParsedDateTime targetDateTime;
    /** Target start date-time for event rescheduling. */
    private final ParsedDateTime eventFromDateTime;
    /** Target end date-time for event rescheduling. */
    private final ParsedDateTime eventToDateTime;
    /** Indicates whether this request is a relative duration snooze. */
    private final boolean isRelative;
    /** Indicates whether this request is an event boundary reschedule. */
    private final boolean isEventReschedule;

    /**
     * Constructs a relative duration SnoozeRequest.
     *
     * @param index 0-based task index.
     * @param relativeAmount Amount of time units to add.
     * @param relativeUnit Unit of time to add.
     */
    public SnoozeRequest(int index, long relativeAmount, ChronoUnit relativeUnit) {
        assert index >= 0 : "Task index must be non-negative";
        assert relativeUnit != null : "ChronoUnit cannot be null";
        this.index = index;
        this.relativeAmount = relativeAmount;
        this.relativeUnit = relativeUnit;
        this.targetDateTime = null;
        this.eventFromDateTime = null;
        this.eventToDateTime = null;
        this.isRelative = true;
        this.isEventReschedule = false;
    }

    /**
     * Constructs an absolute target date-time SnoozeRequest for a deadline.
     *
     * @param index 0-based task index.
     * @param targetDateTime Target date-time for the deadline.
     */
    public SnoozeRequest(int index, ParsedDateTime targetDateTime) {
        assert index >= 0 : "Task index must be non-negative";
        assert targetDateTime != null : "Target date-time cannot be null";
        this.index = index;
        this.relativeAmount = 0;
        this.relativeUnit = null;
        this.targetDateTime = targetDateTime;
        this.eventFromDateTime = null;
        this.eventToDateTime = null;
        this.isRelative = false;
        this.isEventReschedule = false;
    }

    /**
     * Constructs an absolute boundary reschedule SnoozeRequest for an event.
     *
     * @param index 0-based task index.
     * @param eventFromDateTime Target start date-time for the event.
     * @param eventToDateTime Target end date-time for the event.
     */
    public SnoozeRequest(int index, ParsedDateTime eventFromDateTime, ParsedDateTime eventToDateTime) {
        assert index >= 0 : "Task index must be non-negative";
        assert eventFromDateTime != null : "Event start date-time cannot be null";
        assert eventToDateTime != null : "Event end date-time cannot be null";
        this.index = index;
        this.relativeAmount = 0;
        this.relativeUnit = null;
        this.targetDateTime = null;
        this.eventFromDateTime = eventFromDateTime;
        this.eventToDateTime = eventToDateTime;
        this.isRelative = false;
        this.isEventReschedule = true;
    }

    /**
     * Returns the 0-based index of the target task.
     *
     * @return 0-based task index.
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Returns whether this snooze request is a relative duration adjustment.
     *
     * @return True if relative, false if absolute.
     */
    public boolean isRelative() {
        return this.isRelative;
    }

    /**
     * Applies this snooze request to the specified task.
     *
     * @param task Target task to snooze or reschedule.
     * @throws DaveCommandException If the task cannot be snoozed or does not match the request type.
     */
    public void applyTo(Task task) throws DaveCommandException {
        assert task != null : "Target task cannot be null when applying snooze";

        if (!task.canSnooze()) {
            throw new DaveCommandException(
                    "Pardon, Sire! Todo tasks cannot be snoozed as they have no appointed date or time.");
        }

        if (this.isRelative) {
            applyRelativeSnooze(task);
        } else if (this.isEventReschedule) {
            applyEventReschedule(task);
        } else {
            applyDeadlineReschedule(task);
        }
    }

    /**
     * Applies a relative duration adjustment to a deadline or event.
     *
     * @param task Target task to adjust.
     * @throws DaveCommandException If the task cannot accept relative adjustments.
     */
    private void applyRelativeSnooze(Task task) throws DaveCommandException {
        if (task instanceof Deadline deadline) {
            deadline.snoozeBy(this.relativeAmount, this.relativeUnit);
        } else if (task instanceof Event event) {
            event.snoozeBy(this.relativeAmount, this.relativeUnit);
        } else {
            throw new DaveCommandException("Pardon, Sire! This quest cannot be snoozed.");
        }
    }

    /**
     * Reschedules an event with new start and end boundaries.
     *
     * @param task Target event task.
     * @throws DaveCommandException If the task is not an Event.
     */
    private void applyEventReschedule(Task task) throws DaveCommandException {
        if (!(task instanceof Event event)) {
            throw new DaveCommandException("Pardon, Sire! A deadline cannot be rescheduled with /from and /to.");
        }
        event.reschedule(
                this.eventFromDateTime.getDateTime(), this.eventFromDateTime.hasTime(),
                this.eventToDateTime.getDateTime(), this.eventToDateTime.hasTime());
    }

    /**
     * Reschedules a deadline with a new target date-time.
     *
     * @param task Target deadline task.
     * @throws DaveCommandException If the task is not a Deadline.
     */
    private void applyDeadlineReschedule(Task task) throws DaveCommandException {
        if (!(task instanceof Deadline deadline)) {
            throw new DaveCommandException("Pardon, Sire! Rescheduling an event requires /from [time] and /to [time], "
                    + "or a relative duration.");
        }
        deadline.snoozeTo(this.targetDateTime.getDateTime(), this.targetDateTime.hasTime());
    }
}
