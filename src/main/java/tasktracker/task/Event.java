package tasktracker.task;

/**
 * Represents an {@code Event} task that occurs within a specified start and end time range.
 */
public class Event extends Task {
    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Constructs an {@code Event} instance with the specified description, start time, and end time.
     *
     * @param description Text describing the event task.
     * @param from Start time or start date description.
     * @param to End time or end date description.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Gets the start date and time of the event.
     *
     * @return The start {@code TaskDateTime}.
     */
    public TaskDateTime getFrom() {
        return this.from;
    }

    /**
     * Gets the end date and time of the event.
     *
     * @return The end {@code TaskDateTime}.
     */
    public TaskDateTime getTo() {
        return this.to;
    }

    /**
     * Formats the event task for file storage.
     *
     * @return Pipe-delimited string representing the event task prefixed with {@code "E"}.
     */
    @Override
    public String toFileFormat() {
        return "E | " + toFileFormatPrefix() + " | "
                + from.toFileString() + " | " + to.toFileString();
    }

    /**
     * Returns the formatted string representation of the event task.
     *
     * @return String representation containing status, description, and time range prefixed with {@code "[E]"}.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.toDisplayString()
                + " to: " + to.toDisplayString() + ")";
    }
}
