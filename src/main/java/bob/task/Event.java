package bob.task;

import java.time.LocalDateTime;

import bob.exception.BobException;
import bob.util.DatetimeHelper;

/**
 * Represents an event task with a start time and an end time.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Constructs an Event task with the specified description, start time, and end
     * time.
     *
     * @param name the description of the event task
     * @param from the start date/time
     * @param to   the end date/time
     * @throws BobException if from > to
     */
    public Event(String name, LocalDateTime from, LocalDateTime to) throws BobException {
        super(name);
        assert from != null : "Event start date-time should not be null";
        assert to != null : "Event end date-time should not be null";
        this.from = from;
        this.to = to;
        if (from.isAfter(to)) {
            throw new BobException("Error: from date is after to date");
        }
    }

    /**
     * Returns the start date-time for this event.
     *
     * @return the event start date-time
     */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /**
     * Returns the end date-time for this event.
     *
     * @return the event end date-time
     */
    public LocalDateTime getTo() {
        return this.to;
    }

    /**
     * Returns the string representation of the event task, including its status,
     * description, and formatted start and end date/times.
     *
     * @return formatted string representation of this event task
     */
    @Override
    public String toString() {
        return String.format(
                "[E]%s (from: %s to: %s)",
                super.toString(),
                this.from.format(DatetimeHelper.OUTPUT_FORMATTER),
                this.to.format(DatetimeHelper.OUTPUT_FORMATTER));
    }

    /**
     * Formats the event task as a string suitable for persistent storage export.
     *
     * @return delimiter-separated string representation of this event task
     */
    @Override
    public String export() {
        return String.format(
                "E%c%s%c%s%c%s%c%s",
                STORAGE_DELIMITER, this.getDoneCode(),
                STORAGE_DELIMITER, this.name,
                STORAGE_DELIMITER, this.from.format(DatetimeHelper.ISO_FORMATTER),
                STORAGE_DELIMITER,
                this.to.format(DatetimeHelper.ISO_FORMATTER));
    }

    /**
     * Creates an updated copy of this {@link Event} task with optional new description, start time, and end time.
     *
     * @param newName     the new description, or null if unchanged
     * @param newDeadline the new deadline date-time (must be null for Event)
     * @param newFrom     the new start date-time, or null if unchanged
     * @param newTo       the new end date-time, or null if unchanged
     * @return a new updated {@link Event} instance with completion status preserved
     * @throws BobException if deadline flag (/by) is provided or chronology validation fails
     */
    @Override
    public Task update(String newName, LocalDateTime newDeadline,
            LocalDateTime newFrom, LocalDateTime newTo) throws BobException {
        if (newDeadline != null) {
            throw new BobException("Error: Event tasks do not have a deadline (/by)");
        }

        String targetName = newName != null ? newName : this.name;
        LocalDateTime targetFrom = newFrom != null ? newFrom : this.from;
        LocalDateTime targetTo = newTo != null ? newTo : this.to;
        Event updated = new Event(targetName, targetFrom, targetTo);
        if (this.isDone) {
            updated.mark();
        }
        return updated;
    }
}
