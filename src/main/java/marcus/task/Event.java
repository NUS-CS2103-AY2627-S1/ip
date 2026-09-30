package marcus.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a task that takes place between a start and end time.
 */
public class Event extends Task {
    /** Input pattern for event timestamps that can be searched by {@code find}. */
    private static final DateTimeFormatter EVENT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    private final String from;
    private final String to;
    /** Start time parsed from an ISO-formatted event timestamp, when one is provided. */
    private final LocalDateTime fromDateTime;
    /** End time parsed from an ISO-formatted event timestamp, when one is provided. */
    private final LocalDateTime toDateTime;

    /**
     * Creates an incomplete event task.
     *
     * @param description description of the event.
     * @param from event start time.
     * @param to event end time.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
        this.fromDateTime = parseDateTime(from);
        this.toDateTime = parseDateTime(to);
    }

    /**
     * Reports whether this event includes the specified date.
     * Events entered with free-text dates cannot be matched by {@code find}.
     *
     * @param date date to check.
     * @return whether the event has ISO date/times covering {@code date}.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return fromDateTime != null && toDateTime != null
                && !date.isBefore(fromDateTime.toLocalDate())
                && !date.isAfter(toDateTime.toLocalDate());
    }

    /**
     * Parses an ISO event timestamp into a {@link LocalDateTime}.
     *
     * @param dateTimeText event time text in {@code yyyy-mm-dd HH:mm} format.
     * @return the parsed time, or {@code null} when the text is not in the ISO format.
     */
    private static LocalDateTime parseDateTime(String dateTimeText) {
        try {
            return LocalDateTime.parse(dateTimeText, EVENT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Returns this event in its user-facing display format.
     *
     * @return event type, status, description, start time, and end time.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toStringWithoutTags() {
        return "[E]" + super.toStringWithoutTags() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns this event in the format used for persistence.
     *
     * @return event type, completion state, description, start time, and end time.
     */
    @Override
    public String toFileString() {
        return "E | " + (isDone ? "1" : "0") + " | " + description + " | " + from + " | " + to
                + getTagsFileSuffix();
    }
}
