package turtley.model;

import java.time.temporal.Temporal;
import java.util.Collection;

import turtley.exception.TurtleyException;
import turtley.util.DateTimeParser;

/**
 * A task that takes place during a specified time range.
 */
public class Event extends Task {

    private final Temporal from;
    private final Temporal to;

    /**
     * Creates an unfinished event.
     *
     * @param description the event description.
     * @param from the parsed event start date or date-time.
     * @param to the parsed event end date or date-time.
     * @throws TurtleyException if a time value is null or the start is not before the end.
     */
    public Event(String description, Temporal from, Temporal to) {
        super(TaskType.EVENT, description);
        validateTimeRange(from, to);
        this.from = from;
        this.to = to;
    }

    /**
     * Creates an unfinished event with tags.
     *
     * @param description the event description.
     * @param from the parsed event start date or date-time.
     * @param to the parsed event end date or date-time.
     * @param tags the event tags.
     * @throws TurtleyException if a time value is null, the start is not before the end, or the tags are invalid.
     */
    public Event(String description, Temporal from, Temporal to, Collection<String> tags) {
        super(TaskType.EVENT, description, tags);
        validateTimeRange(from, to);
        this.from = from;
        this.to = to;
    }

    /**
     * Creates an unfinished event from user-entered date/time text.
     *
     * @param description the event description.
     * @param from the event start text.
     * @param to the event end text.
     */
    public Event(String description, String from, String to) {
        this(description, DateTimeParser.parse(from), DateTimeParser.parse(to));
    }

    /**
     * Creates an unfinished event from user-entered date/time text with tags.
     *
     * @param description the event description.
     * @param from the event start text.
     * @param to the event end text.
     * @param tags the event tags.
     */
    public Event(String description, String from, String to, Collection<String> tags) {
        this(description, DateTimeParser.parse(from), DateTimeParser.parse(to), tags);
    }

    /**
     * Returns the parsed event start value.
     *
     * @return the start as a {@link java.time.LocalDate} or {@link java.time.LocalDateTime}
     */
    public Temporal getFrom() {
        return from;
    }

    /**
     * Returns the parsed event end value.
     *
     * @return the end as a {@link java.time.LocalDate} or {@link java.time.LocalDateTime}
     */
    public Temporal getTo() {
        return to;
    }

    private static void validateTimeRange(Temporal from, Temporal to) {
        if (from == null || to == null) {
            throw new TurtleyException("Event start and end date/time values cannot be null.");
        }
        if (!DateTimeParser.isBefore(from, to)) {
            throw new TurtleyException("Event start time must be before end time.");
        }
    }

    /**
     * Returns the display form including the event time range.
     *
     * @return the formatted event.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimeParser.format(from)
                + " to: " + DateTimeParser.format(to) + ")";
    }
}
