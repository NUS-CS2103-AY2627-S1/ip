package jonathan.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event task that spans a specific duration with a start and end date.
 */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Constructs an {@code Event} task with a description, start date, and end date.
     * The date strings must be in a format parseable by {@code LocalDate} (e.g., yyyy-mm-dd).
     *
     * @param description The details of the event.
     * @param from        The starting date of the event as a string.
     * @param to          The ending date of the event as a string.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = LocalDate.parse(from);
        this.to = LocalDate.parse(to);
    }

    /**
     * Checks if a given date falls within the duration of the event (inclusive).
     *
     * @param date The target date to check against the event's duration.
     * @return {@code true} if the date is exactly on or between the start and end dates, {@code false} otherwise.
     */
    @Override
    public boolean isOccuringOn(LocalDate date) {
        if (this.from == null || this.to == null || date == null) {
            return false;
        }
        return !date.isBefore(this.from) && !date.isAfter(this.to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + this.from.format(DateTimeFormatter.ofPattern("MMM d yyyy"))
                + " to: " + this.to.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + ")";
    }

    /**
     * Returns the string representation of the event task for saving to the local data file.
     *
     * @return A formatted string delimited by pipes ("|") containing the task's save state.
     */
    @Override
    public String toFileString() {
        return "E | " + getStatusCode() + " | " + getDescription() + " | "
                + from.format(DateTimeFormatter.ofPattern("MMM d yyyy"))
                + " | " + to.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + this.getPriorityFileSuffix();
    }
}
