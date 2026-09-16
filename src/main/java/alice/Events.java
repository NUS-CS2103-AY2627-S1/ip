package alice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that occurs over a specific date range (from and to).
 * Inherits from the Task class.
 */
public class Events extends Task {
    protected LocalDate from;
    protected LocalDate to;

    /**
     * Constructs an Event task with the given description and date range.
     *
     * @param description The task description.
     * @param from        The start date of the event.
     * @param to          The end date of the event.
     */
    public Events(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date of the event.
     *
     * @return The LocalDate representing the start.
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the end date of the event.
     *
     * @return The LocalDate representing the end.
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * {@inheritDoc}
     * Returns the type icon for an Event task, which is "E".
     */
    @Override
    public String getTaskTypeIcon() {
        return "E";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task copy() {
        Events copy = new Events(description, from, to);
        copy.isDone = isDone;
        return copy;
    }

    /**
     * Returns the string representation of the event task.
     * The format is "[E][status] description (from: MMM d yyyy to: MMM d yyyy)".
     *
     * @return The formatted string.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy");
        return String.format("[E]%s (from: %s to: %s)", super.toString(),
                this.from.format(formatter), this.to.format(formatter));
    }

    /**
     * Returns the start date in ISO-8601 format (yyyy-MM-dd) for file storage.
     *
     * @return The start date string.
     */
    public String getFromForFile() {
        return from.toString();
    }

    /**
     * Returns the end date in ISO-8601 format (yyyy-MM-dd) for file storage.
     *
     * @return The end date string.
     */
    public String getToForFile() {
        return to.toString();
    }
}
