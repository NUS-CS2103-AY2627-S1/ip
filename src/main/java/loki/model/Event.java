package loki.model;

import java.time.LocalDateTime;

import loki.parser.DateTimeParser;
import loki.parser.TaskUpdate;

/**
 * A task scheduled between a specific start time and end time.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates a new unfinished event task.
     *
     * @param title the event description.
     * @param from the event start time, kept in the user's display format.
     * @param to the event end time, kept in the user's display format.
     */
    public Event(String title, String from, String to) {
        this(title, DateTimeParser.parseUserInput(from), DateTimeParser.parseUserInput(to));
    }

    /**
     * Creates an unfinished event from a start and end date/time.
     *
     * @param title the event description
     * @param from the event start time
     * @param to the event end time
     * @throws IllegalArgumentException if either time is missing or the start is after the end
     */
    public Event(String title, LocalDateTime from, LocalDateTime to) {
        this(title, false, from, to);
    }

    /**
     * Creates an event from user-entered date/time text and a completion status.
     *
     * @param title the event description
     * @param done whether the event has been completed
     * @param from the start time in a supported user-entered format
     * @param to the end time in a supported user-entered format
     */
    public Event(String title, boolean done, String from, String to) {
        this(title, done, DateTimeParser.parseUserInput(from), DateTimeParser.parseUserInput(to));
    }

    /**
     * Creates an event from date/time values and a completion status.
     *
     * @param title the event description
     * @param done whether the event has been completed
     * @param from the event start time
     * @param to the event end time
     * @throws IllegalArgumentException if either time is missing or the start is after the end
     */
    public Event(String title, boolean done, LocalDateTime from, LocalDateTime to) {
        this(title, done ? 1 : 0, from, to);
    }

    /**
     * Creates an event from stored date/time text and a numeric status.
     *
     * @param title the event description
     * @param done the completion status, which must be {@code 0} or {@code 1}
     * @param from the stored start time in ISO format
     * @param to the stored end time in ISO format
     */
    public Event(String title, int done, String from, String to) {
        this(title, done, DateTimeParser.parseStored(from), DateTimeParser.parseStored(to));
    }

    /**
     * Creates an event from date/time values and a numeric status.
     *
     * @param title the event description
     * @param done the completion status, which must be {@code 0} or {@code 1}
     * @param from the event start time
     * @param to the event end time
     * @throws IllegalArgumentException if either time is missing or the start is after the end
     */
    public Event(String title, int done, LocalDateTime from, LocalDateTime to) {
        super(title, done);
        this.from = validateDateTime(from, "Event start time");
        this.to = validateDateTime(to, "Event end time");
        if (this.from.isAfter(this.to)) {
            throw new IllegalArgumentException("Event start must not be after its end");
        }
    }

    /**
     * Ensures that an event date/time is present.
     *
     * @param value the date/time to validate
     * @param fieldName the field name used in an error message
     * @return the validated date/time
     * @throws IllegalArgumentException if {@code value} is {@code null}
     */
    private static LocalDateTime validateDateTime(LocalDateTime value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return value;
    }

    /**
     * Formats this task with its start and end times.
     *
     * @return the formatted event task.
     */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(),
                DateTimeParser.formatForDisplay(from), DateTimeParser.formatForDisplay(to));
    }

    /**
     * Converts this event task into the storage format.
     *
     * @return the serialized event task.
     */
    @Override
    public String saveString() {
        int status = isDone() ? 1 : 0;
        String schedule = DateTimeParser.formatForStorage(from) + " -> "
                + DateTimeParser.formatForStorage(to);
        return String.format("E | %d | %s | %s", status, getTitle(), schedule);
    }

    /**
     * Creates an updated event while preserving its completion status.
     *
     * @param update the replacement fields.
     * @return the updated event.
     * @throws IllegalArgumentException if the update contains a deadline field or reverses the interval.
     */
    @Override
    public Task updatedWith(TaskUpdate update) {
        if (update.getDeadline().isPresent()) {
            throw new IllegalArgumentException("An event does not support deadline fields");
        }
        String title = update.getTitle().orElse(getTitle());
        LocalDateTime updatedFrom = update.getEventStart().orElse(from);
        LocalDateTime updatedTo = update.getEventEnd().orElse(to);
        return new Event(title, isDone(), updatedFrom, updatedTo);
    }
}
