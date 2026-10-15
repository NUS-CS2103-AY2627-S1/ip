package nova.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import nova.exception.NovaException;

/** Represents an event with plain-text start and end date/time values. */
public class Event extends Task {
    /** The format used for event start and end date-times. */
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    /** The plain-text event start date and time. */
    protected String from;

    /** The plain-text event end date and time. */
    protected String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the event
     * @param from the plain-text start date and time
     * @param to the plain-text end date and time
     */
    public Event(String description, String from, String to) throws NovaException {
        super(description, TaskType.EVENT);
        if (!isValidDateTimeRange(from, to)) {
            throw new NovaException("Event start and end must be valid, and start must be before end.");
        }
        this.from = from;
        this.to = to;
    }

    /** Parses an event date-time in the supported format. */
    public static LocalDateTime parseDateTime(String value) {
        return LocalDateTime.parse(value, DATE_TIME_FORMAT);
    }

    /** Returns whether two event date-times are valid and strictly chronological. */
    public static boolean isValidDateTimeRange(String from, String to) {
        try {
            return parseDateTime(from).isBefore(parseDateTime(to));
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    /** Returns whether another task has the same type, description, and event date-time range. */
    @Override
    public boolean hasSameIdentity(Task other) {
        return super.hasSameIdentity(other) && other instanceof Event event
                && from.equals(event.from) && to.equals(event.to);
    }

    /** Returns the event description with its time-range suffix. */
    @Override
    public String getDisplayDescription() {
        return description + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns this event in the format used for persistent storage.
     *
     * @return the event type, completion state, description, start, and end separated by pipes
     */
    @Override
    public String toStorageString() {
        return getBaseStorageString() + " | " + from + " | " + to + getTagsStorageSuffix();
    }

    /**
     * Returns this event task with its type prefix and time range.
     *
     * @return the event display string
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
