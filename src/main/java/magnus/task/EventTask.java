package magnus.task;

import java.time.LocalDateTime;
import java.util.Objects;

import magnus.parser.DateTimeFormats;
import magnus.parser.DateTimeParser;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class EventTask extends Task {
    private static final String EVENT_TIME_DELIMITER = "-";
    private static final DateTimeParser EVENT_PARSER = new DateTimeParser();

    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Creates an incomplete event task.
     *
     * @param description The description of the event.
     * @param start The event's start time.
     * @param end The event's end time.
     */
    public EventTask(String description, String start, String end) {
        this(description,
                EVENT_PARSER.parseDateTime(start, "start time"),
                EVENT_PARSER.parseDateTime(end, "end time"));
    }

    /**
     * Creates an incomplete event task with already-parsed start and end times.
     *
     * @param description The description of the event.
     * @param start The event's start time.
     * @param end The event's end time.
     */
    public EventTask(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
        if (!this.start.isBefore(this.end)) {
            throw new IllegalArgumentException("Event start time must be before end time");
        }
    }

    /**
     * Returns the event's start time as a date-time value.
     *
     * @return The event's start time.
     */
    public LocalDateTime getStart() {
        return this.start;
    }

    /**
     * Returns the event's end time as a date-time value.
     *
     * @return The event's end time.
     */
    public LocalDateTime getEnd() {
        return this.end;
    }

    /**
     * Formats an event time in the canonical form accepted from user input and storage.
     *
     * @param eventTime The event time to format.
     * @return The formatted event time.
     */
    private String formatForStorage(LocalDateTime eventTime) {
        return eventTime.format(DateTimeFormats.FORMATTER_DATE_TIME);
    }

    /**
     * Formats an event time for display with an English abbreviated month.
     *
     * @param eventTime The event time to format.
     * @return The display-formatted event time.
     */
    private String formatForDisplay(LocalDateTime eventTime) {
        return eventTime.format(DateTimeFormats.FORMATTER_DISPLAY_DATE_TIME);
    }

    @Override
    protected TaskType getTaskType() {
        return TaskType.EVENT;
    }

    @Override
    protected boolean hasSameSchedule(Task other) {
        EventTask otherEvent = (EventTask) other;
        return this.start.equals(otherEvent.start) && this.end.equals(otherEvent.end);
    }

    /**
     * Serializes this event task into the format used by persistent storage.
     *
     * @return The serialized event-task data.
     */
    @Override
    public String serialize() {
        String eventTime = formatForStorage(this.start)
                + EVENT_TIME_DELIMITER + formatForStorage(this.end);
        return String.format("%s,%d,%s,%s",
                getTaskType().getStorageCode(), getCompletionStatusCode(),
                encodeDataField(getDescription()), encodeDataField(eventTime));
    }

    /**
     * Returns a string representation containing the event-task marker, completion status,
     * description, start time, and end time.
     *
     * @return The string representation of this event task.
     */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(),
                formatForDisplay(this.start), formatForDisplay(this.end));
    }
}
