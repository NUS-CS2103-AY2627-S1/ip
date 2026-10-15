package orbit.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that takes place between two date or time descriptions.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates a new event task.
     *
     * @param description description of the event
     * @param from date when the event starts
     * @param to date when the event ends
     * @throws IllegalArgumentException if the end date is before the start date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description, TaskType.EVENT);
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Event end date cannot be before its start date");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Checks whether this event's inclusive date range contains a date.
     *
     * @param date date whose schedule is being viewed
     * @return true if the event occurs on the date
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    /**
     * Returns the event task in its display format.
     *
     * @return the task type, status, description, start, and end
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns a representation of this event suitable for saving to a file.
     *
     * @return pipe-separated event data
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + from + " | " + to;
    }
}
