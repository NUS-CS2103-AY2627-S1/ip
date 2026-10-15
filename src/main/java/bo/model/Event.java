package bo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import bo.time.DateTimeParser;

/**
 * A task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    /** The parsed date/time at which this event starts. */
    private final LocalDateTime from;

    /** The parsed date/time at which this event ends. */
    private final LocalDateTime to;

    /** Whether the original start value included a time. */
    private final boolean includesFromTime;

    /** Whether the original end value included a time. */
    private final boolean includesToTime;

    /** The original free-form start value, if it could not be parsed. */
    private final String legacyFrom;

    /** The original free-form end value, if it could not be parsed. */
    private final String legacyTo;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the task.
     * @param from the date or time at which the event starts.
     * @param to the date or time at which the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        if (from == null || from.isBlank() || to == null || to.isBlank()) {
            throw new IllegalArgumentException("An event must have non-blank times.");
        }
        DateTimeParser.ParsedDateTime parsedFrom = tryParse(from);
        DateTimeParser.ParsedDateTime parsedTo = tryParse(to);
        this.from = parsedFrom == null ? null : parsedFrom.value();
        this.to = parsedTo == null ? null : parsedTo.value();
        this.includesFromTime = parsedFrom != null && parsedFrom.includesTime();
        this.includesToTime = parsedTo != null && parsedTo.includesTime();
        this.legacyFrom = parsedFrom == null ? from : null;
        this.legacyTo = parsedTo == null ? to : null;
        validateRange(this.from, this.to);
    }

    /**
     * Creates an incomplete event with date-only start and end values.
     *
     * @param description the text describing the event.
     * @param from the event start date.
     * @param to the event end date.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        if (from == null || to == null) {
            throw new IllegalArgumentException("An event must have non-null dates.");
        }
        this.from = from.atStartOfDay();
        this.to = to.atStartOfDay();
        this.includesFromTime = false;
        this.includesToTime = false;
        this.legacyFrom = null;
        this.legacyTo = null;
        validateRange(this.from, this.to);
    }

    /**
     * Creates an incomplete event with date and time start and end values.
     *
     * @param description the text describing the event.
     * @param from the event start date and time.
     * @param to the event end date and time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        if (from == null || to == null) {
            throw new IllegalArgumentException("An event must have non-null date/times.");
        }
        this.from = from;
        this.to = to;
        this.includesFromTime = true;
        this.includesToTime = true;
        this.legacyFrom = null;
        this.legacyTo = null;
        validateRange(this.from, this.to);
    }

    /**
     * Returns the parsed event start value.
     *
     * @return the event start as a {@link LocalDateTime}, or {@code null} for
     *         an old free-form value that could not be parsed.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the parsed event end value.
     *
     * @return the event end as a {@link LocalDateTime}, or {@code null} for an
     *         old free-form value that could not be parsed
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns the event start value used when displaying the task.
     *
     * @return a formatted date/time, or the old free-form value.
     */
    public String getDisplayFrom() {
        if (legacyFrom != null) {
            return legacyFrom;
        }
        return DateTimeParser.format(new DateTimeParser.ParsedDateTime(from, includesFromTime));
    }

    /**
     * Returns the event end value used when displaying the task.
     *
     * @return a formatted date/time, or the old free-form value.
     */
    public String getDisplayTo() {
        if (legacyTo != null) {
            return legacyTo;
        }
        return DateTimeParser.format(new DateTimeParser.ParsedDateTime(to, includesToTime));
    }

    /**
     * Returns the event start value used in the storage file.
     *
     * @return an ISO date/time, or the old free-form value.
     */
    public String getStoredFrom() {
        if (legacyFrom != null) {
            return legacyFrom;
        }
        return DateTimeParser.serialize(new DateTimeParser.ParsedDateTime(from, includesFromTime));
    }

    /**
     * Returns the event end value used in the storage file.
     *
     * @return an ISO date/time, or the old free-form value.
     */
    public String getStoredTo() {
        if (legacyTo != null) {
            return legacyTo;
        }
        return DateTimeParser.serialize(new DateTimeParser.ParsedDateTime(to, includesToTime));
    }

    /**
     * Returns the icon used for event tasks.
     *
     * @return {@code E}.
     */
    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * Returns the task including its start and end values.
     *
     * @return the formatted event task.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + getDisplayFrom() + " to: " + getDisplayTo() + ")";
    }

    /**
     * Tries to parse a value while retaining old free-form values from earlier
     * versions of Bo.
     *
     * @param value the value to parse.
     * @return the parsed value, or {@code null} when it is legacy text.
     */
    private static DateTimeParser.ParsedDateTime tryParse(String value) {
        try {
            return DateTimeParser.parse(value);
        } catch (IllegalArgumentException exception) {
            if (DateTimeParser.looksLikeStructuredDateTime(value)) {
                throw exception;
            }
            return null;
        }
    }

    /** Checks that typed event times form a strictly increasing interval. */
    private static void validateRange(LocalDateTime from, LocalDateTime to) {
        if (from != null && to != null && !from.isBefore(to)) {
            throw new IllegalArgumentException("An event must start before it ends.");
        }
    }
}
