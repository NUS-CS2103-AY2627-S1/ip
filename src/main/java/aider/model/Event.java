package aider.model;

import java.time.LocalDateTime;

import aider.util.DateTimeParser;

/**
 * A task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    /** The date or time when the event starts. */
    private final LocalDateTime from;

    /** The date or time when the event ends. */
    private final LocalDateTime to;

    /** The start and end text entered when the event was created. */
    private final String scheduleText;

    /**
     * Creates an event task.
     *
     * @param description the text describing the event
     * @param from the event start date or time
     * @param to the event end date or time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        this(description, from, to, DateTimeParser.format(from), DateTimeParser.format(to));
    }

    /**
     * Creates an event task while preserving its original schedule text.
     *
     * @param description the text describing the event
     * @param from the parsed event start date or time
     * @param to the parsed event end date or time
     * @param fromText the start text entered by the user
     * @param toText the end text entered by the user
     */
    public Event(String description, LocalDateTime from, LocalDateTime to,
            String fromText, String toText) {
        super(description, TaskType.EVENT);
        assert from != null : "Event start time must not be null";
        assert to != null : "Event end time must not be null";
        this.from = from;
        this.to = to;
        this.scheduleText = fromText + " " + toText;
    }

    /**
     * Returns the event start date and time.
     *
     * @return the event start date and time
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end date and time.
     *
     * @return the event end date and time
     */
    public LocalDateTime getTo() {
        return to;
    }

    @Override
    protected String getScheduleText() {
        return scheduleText;
    }

    @Override
    protected String getTaskDetails() {
        return description + " (from: " + DateTimeParser.format(from)
                + " to: " + DateTimeParser.format(to) + ")";
    }

    @Override
    public String toFileString() {
        return "E | " + (isDone ? "1" : "0") + " | " + description + " | "
                + DateTimeParser.toStorageString(from) + " | "
                + DateTimeParser.toStorageString(to);
    }
}
