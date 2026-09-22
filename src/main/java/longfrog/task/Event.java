package longfrog.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import longfrog.util.FormatUtils;

/** Represents a task that occurs over a start and end date-time range. */
public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param name the task description
     * @param from the event start date and time
     * @param to the event end date and time
     */
    public Event(String name, LocalDateTime from, LocalDateTime to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start date and time.
     *
     * @return the event start
     */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /**
     * Returns the event end date and time.
     *
     * @return the event end
     */
    public LocalDateTime getTo() {
        return this.to;
    }

    /**
     * Checks whether another task has the same description and time range.
     *
     * @param other the task to compare with
     * @return whether the tasks have the same duplicate identity
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        return super.isDuplicateOf(other)
                && from.equals(((Event) other).from)
                && to.equals(((Event) other).to);
    }

    /**
     * Checks whether this event's date range includes a date.
     *
     * @param date the date to check
     * @return whether the event occurs on the date
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = from.toLocalDate();
        LocalDate endDate = to.toLocalDate();
        boolean isOnOrAfterStart = !date.isBefore(startDate);
        boolean isOnOrBeforeEnd = !date.isAfter(endDate);

        return isOnOrAfterStart && isOnOrBeforeEnd;
    }

    /**
     * Returns this event in its console display format.
     *
     * @return the formatted event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + this.from.format(FormatUtils.PRINT_FORMAT)
                + " to: " + this.to.format(FormatUtils.PRINT_FORMAT) + ")";
    }

    /**
     * Returns this event in the format used by the save file.
     *
     * @return the serialized event
     */
    @Override
    public String toFileFormat() {
        return "E | " + (isDone ? "1" : "0") + " | " + this.name + " | "
                + this.from.format(FormatUtils.INPUT_SAVE_FORMAT) + " | "
                + this.to.format(FormatUtils.INPUT_SAVE_FORMAT);
    }
}
