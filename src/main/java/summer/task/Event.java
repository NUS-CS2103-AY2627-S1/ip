package summer.task;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a task that starts and ends on specific dates.
 */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event task with the given description, start, end, and completion status.
     *
     * @param description text describing the task
     * @param from date when the task starts
     * @param to date when the task ends
     * @param isDone whether the task is already completed
     */
    public Event(String description, LocalDate from, LocalDate to, boolean isDone) {
        super(TaskType.EVENT, description, isDone);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the display text for this event task.
     *
     * @return task type, status, description, start date, and end date
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + this.from.format(DATE_DISPLAY_FORMAT)
                + " to: " + this.to.format(DATE_DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns this event's save line, with the start and end dates appended.
     *
     * @return pipe-delimited save line for this event
     */
    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + " | " + this.from + " | " + this.to;
    }

    /**
     * Checks whether the given date falls within this event's start and end dates, inclusive.
     *
     * @param date date to check
     * @return true if the given date is within this event's start and end dates
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(this.from) && !date.isAfter(this.to);
    }

    /**
     * Returns this event's start date, used to order it when sorting.
     *
     * @return the start date
     */
    @Override
    public Optional<LocalDate> sortKey() {
        return Optional.of(this.from);
    }
}
