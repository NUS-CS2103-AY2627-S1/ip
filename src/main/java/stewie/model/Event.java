package stewie.model;

/**
 * Represents a task with a start date strictly before its end date.
 */
public class Event extends Task {
    private final Date from;
    private final Date to;

    /**
     * Creates an unfinished event with a validated description and date range.
     *
     * @param description Description of the event.
     * @param from Start date of the event in a supported format.
     * @param to End date of the event in a supported format.
     * @throws IllegalArgumentException If the description or a date is invalid, or the start is not before the end.
     */
    public Event(String description, String from, String to)
            throws IllegalArgumentException {
        super(description);
        validateArgument(from, "Event start time");
        validateArgument(to, "Event end time");
        this.from = new Date(from);
        this.to = new Date(to);
        if (!this.from.isBefore(this.to)) {
            throw new IllegalArgumentException("Event start date must be before its end date.");
        }
        assert this.from != null && this.to != null : "An event must have both boundary dates";
    }

    /**
     * Returns the event start date formatted as {@code dd MMM uuuu}.
     */
    public String getFrom() {
        return this.from.toString();
    }

    /**
     * Returns the event end date formatted as {@code dd MMM uuuu}.
     */
    public String getTo() {
        return this.to.toString();
    }

    /**
     * Returns the event type marker, completion marker, description, and formatted date range.
     */
    @Override
    public String toString() {
        return String.format("[E] [%s] %s (from: %s to: %s)", super.getStatusIcon(),
                super.getDescription(),
                this.from.toString(),
                this.to.toString()
        );
    }
}
