package trybot.task;

import java.time.LocalDateTime;

import trybot.parser.DateTimeParser;

/**
 * A task with a specified start and end date or time.
 */
public class Event extends Task {
    private final String fromText;
    private final String toText;
    private final LocalDateTime fromDateTime;
    private final LocalDateTime toDateTime;
    private final boolean fromHasTime;
    private final boolean toHasTime;

    /**
     * Creates an incomplete event task.
     *
     * @param description text describing the event.
     * @param from date or time when the event starts.
     * @param to date or time when the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        if (from == null || from.isBlank() || to == null || to.isBlank()) {
            throw new IllegalArgumentException("An event needs both a start and end time.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("An event description cannot be blank.");
        }
        DateTimeParser.ParsedDateTime parsedFrom = DateTimeParser.parseOrNull(from);
        DateTimeParser.ParsedDateTime parsedTo = DateTimeParser.parseOrNull(to);
        if (parsedFrom != null && parsedTo != null) {
            if (parsedFrom.dateTime().isAfter(parsedTo.dateTime())) {
                throw new IllegalArgumentException("An event cannot end before it starts.");
            }
            if (parsedFrom.dateTime().equals(parsedTo.dateTime())) {
                throw new IllegalArgumentException("An event must end after it starts.");
            }
        }
        String trimmedFrom = from.trim();
        String trimmedTo = to.trim();
        this.fromText = parsedFrom == null ? trimmedFrom : null;
        this.toText = parsedTo == null ? trimmedTo : null;
        this.fromDateTime = parsedFrom == null ? null : parsedFrom.dateTime();
        this.toDateTime = parsedTo == null ? null : parsedTo.dateTime();
        this.fromHasTime = parsedFrom != null && parsedFrom.hasTime();
        this.toHasTime = parsedTo != null && parsedTo.hasTime();

        // Each endpoint must have exactly one representation after construction.
        assert (fromText == null) != (fromDateTime == null);
        assert (toText == null) != (toDateTime == null);
    }

    /**
     * Returns the typed event start when it is a supported numeric date or date-time.
     *
     * @return event start date-time, or null for legacy descriptive text
     */
    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    /**
     * Returns the typed event end when it is a supported numeric date or date-time.
     *
     * @return event end date-time, or null for legacy descriptive text
     */
    public LocalDateTime getToDateTime() {
        return toDateTime;
    }

    /**
     * Returns the event start as it should be shown to the user.
     *
     * @return formatted date-time or the original descriptive text
     */
    public String getFrom() {
        return fromDateTime == null ? fromText
                : DateTimeParser.formatForDisplay(new DateTimeParser.ParsedDateTime(fromDateTime, fromHasTime));
    }

    /**
     * Returns the event end as it should be shown to the user.
     *
     * @return formatted date-time or the original descriptive text
     */
    public String getTo() {
        return toDateTime == null ? toText
                : DateTimeParser.formatForDisplay(new DateTimeParser.ParsedDateTime(toDateTime, toHasTime));
    }

    /**
     * Returns the canonical event start value used in the task data file.
     *
     * @return storage-formatted date-time or the original descriptive text
     */
    private String getFromStorageValue() {
        return fromDateTime == null ? fromText
                : DateTimeParser.formatForStorage(new DateTimeParser.ParsedDateTime(fromDateTime, fromHasTime));
    }

    /**
     * Returns the canonical event end value used in the task data file.
     *
     * @return storage-formatted date-time or the original descriptive text
     */
    private String getToStorageValue() {
        return toDateTime == null ? toText
                : DateTimeParser.formatForStorage(new DateTimeParser.ParsedDateTime(toDateTime, toHasTime));
    }

    /**
     * Formats this event for the task data file.
     *
     * @return the task type, completion status, description, and event times
     */
    @Override
    public String toStorageString() {
        return "E | " + (isDone ? "1" : "0") + " | " + escapeStorageField(description)
                + " | " + escapeStorageField(getFromStorageValue())
                + " | " + escapeStorageField(getToStorageValue()) + getTagsForStorage();
    }

    /**
     * Formats this task with its start and end times.
     *
     * @return formatted event task
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + getFrom() + " to: " + getTo() + ")";
    }
}
