package phin;

import java.time.LocalDate;

/**
 * Represents a task that takes place between specified start and end times.
 */
public class Event extends Task {
    /**
     * First calendar date of the event.
     */
    protected LocalDate from;
    /**
     * Last calendar date of the event.
     */
    protected LocalDate to;

    /**
     * Creates an event task that has not been completed yet.
     *
     * @param description description of the event.
     * @param from start date in yyyy-MM-dd format.
     * @param to end date in yyyy-MM-dd format.
     * @throws IllegalArgumentException if a date is invalid or the end precedes the start.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = TaskDate.parse(from);
        this.to = TaskDate.parse(to);
        if (this.to.isBefore(this.from)) {
            throw new IllegalArgumentException("An event's end date cannot be before its start date.");
        }
    }

    /**
     * Formats this task with its type indicator and time range.
     *
     * @return the formatted event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + TaskDate.format(from)
                + " to: " + TaskDate.format(to) + ")";
    }
}
