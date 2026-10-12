package braun.task;

import java.time.LocalDate;
import java.util.Objects;

import braun.exception.BraunException;
import braun.util.DateTimeUtil;

/**
 * Represents an event task that starts at a specific date/time and ends at a specific date/time.
 * Encapsulates intervals using {@link DateTimeUtil.TaskDateTime}.
 */
public class Event extends Task {

    /** The start date/time of the event */
    protected DateTimeUtil.TaskDateTime from;

    /** The end date/time of the event */
    protected DateTimeUtil.TaskDateTime to;

    /**
     * Constructs a new {@code Event} task by parsing the specified start and end date/time strings.
     * If the end time is given without a date, the start date is used by default.
     *
     * @param description the textual description of the event.
     * @param from the start date/time string.
     * @param to the end date/time string.
     * @throws BraunException if either date/time is invalid or if end time is before start time.
     */
    public Event(String description, String from, String to) throws BraunException {
        super(description);
        this.from = DateTimeUtil.parse(from);
        this.to = DateTimeUtil.parse(to, this.from.getDate());

        if (this.to.isBefore(this.from)) {
            throw new BraunException("*static* Event end time cannot be before start time!");
        }
    }

    /**
     * Constructs a new {@code Event} task with pre-parsed {@link DateTimeUtil.TaskDateTime} instances.
     *
     * @param description the textual description of the event.
     * @param from the start date/time object.
     * @param to the end date/time object.
     * @throws BraunException if end time is before start time.
     */
    public Event(String description, DateTimeUtil.TaskDateTime from, DateTimeUtil.TaskDateTime to)
            throws BraunException {
        super(description);
        this.from = from;
        this.to = to;

        if (this.to.isBefore(this.from)) {
            throw new BraunException("*static* Event end time cannot be before start time!");
        }
    }

    /**
     * Returns the structured start date/time of the event.
     *
     * @return the start date/time object.
     */
    public DateTimeUtil.TaskDateTime getFrom() {
        return from;
    }

    /**
     * Returns the structured end date/time of the event.
     *
     * @return the end date/time object.
     */
    public DateTimeUtil.TaskDateTime getTo() {
        return to;
    }

    /**
     * Checks if this event occurs on or spans across the specified date.
     *
     * @param queryDate date to compare against.
     * @return {@code true} if the query date falls within [from.date, to.date].
     */
    @Override
    public boolean isOnDate(LocalDate queryDate) {
        return !queryDate.isBefore(from.getDate()) && !queryDate.isAfter(to.getDate());
    }

    /**
     * Returns the formatted string representation of the event task,
     * prefixed with the [E] identifier tag and appending formatted start and end times.
     *
     * @return formatted string such as "[E][ ] project meeting (from: Aug 06 2026, 2:00PM to: Aug 06 2026, 4:00PM)".
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.formatForDisplay() + " to: " + to.formatForDisplay() + ")";
    }

    /**
     * Returns the formatted string representation of the event task for file storage.
     *
     * @return a pipe-delimited string prefixed with the 'E' tag and containing storage date/times.
     */
    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from.formatForFile() + " | " + to.formatForFile();
    }

    /**
     * Compares this event task with another object for equality.
     *
     * @param other the object to compare against.
     * @return {@code true} if both tasks have the same description, start time, and end time; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        Event event = (Event) other;
        return from.formatForFile().equals(event.from.formatForFile())
                && to.formatForFile().equals(event.to.formatForFile());
    }

    /**
     * Returns a hash code based on description, start time, and end time.
     *
     * @return integer hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from.formatForFile(), to.formatForFile());
    }
}
