package sage.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that spans a start time and an end time.
 */
public class Event extends Task {
    /**
     * Start date and time, or {@code null} if no parsed value is available.
     */
    protected LocalDateTime from;

    /**
     * End date and time, or {@code null} if no parsed value is available.
     */
    protected LocalDateTime to;

    /**
     * Text retained as the event's stored start representation.
     */
    protected String fromText;

    /**
     * Text retained as the event's stored end representation.
     */
    protected String toText;

    /**
     * Creates an event from raw text for its start and end times.
     *
     * @param description the event description.
     * @param from the start time text.
     * @param to the end time text.
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.fromText = from == null ? "" : from.trim();
        this.toText = to == null ? "" : to.trim();
        this.from = parseDateTime(this.fromText);
        this.to = parseDateTime(this.toText);
    }

    /**
     * Creates an event from LocalDateTime instances.
     *
     * @param description the event description.
     * @param from the starting date-time.
     * @param to the ending date-time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
        this.fromText = from == null ? "" : from.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.toText = to == null ? "" : to.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Returns the starting date-time for the event.
     *
     * @return the start time, or {@code null} if none is available.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the ending date-time for the event.
     *
     * @return the end time, or {@code null} if none is available.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns the stored textual representation of the event's start.
     *
     * @return the stored start text.
     */
    public String getFromText() {
        return fromText;
    }

    /**
     * Returns the stored textual representation of the event's end.
     *
     * @return the stored end text.
     */
    public String getToText() {
        return toText;
    }

    /**
     * Parses a date or date-time string into a LocalDateTime value.
     *
     * @param rawValue the user-entered time text.
     * @return the parsed time, or {@code null} if the input is not in a supported format.
     */
    public static LocalDateTime parseDateTime(String rawValue) {
        return TaskDateTime.parse(rawValue);
    }

    /**
     * Formats the event as a user-facing string summary.
     *
     * @return the formatted event description for display.
     */
    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "] " + description
                + " (from: " + TaskDateTime.format(from, fromText)
                + " to: " + TaskDateTime.format(to, toText) + ")";
    }
}
