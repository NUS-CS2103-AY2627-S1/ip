package caitlyn;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a task that takes place between a start date/time and an end date/time.
 */
public class Event extends Task {
    /** The date and optional time at which the event starts. */
    private final LocalDateTime from;

    /** Whether the event start input explicitly included a time. */
    private final boolean hasFromTime;

    /** The date and optional time at which the event ends. */
    private final LocalDateTime to;

    /** Whether the event end input explicitly included a time. */
    private final boolean hasToTime;

    /**
     * Creates a new incomplete event task.
     *
     * @param description the text describing the event.
     * @param from the event's start date or time.
     * @param to the event's end date or time.
     * @throws IllegalArgumentException if a field is null, a date is invalid, or the range is reversed.
     */
    public Event(String description, String from, String to) {
        this(description, parseDate(from), parseDate(to));
    }

    /**
     * Creates an event from date-only start and end values.
     *
     * @param description the event description.
     * @param from the event's start date.
     * @param to the event's end date.
     * @throws IllegalArgumentException if a field is null or the range is reversed.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        this(description, convertDateOnly(from), convertDateOnly(to));
    }

    /**
     * Creates an event from start and end date/time values.
     *
     * @param description the event description.
     * @param from the event's start date and time.
     * @param to the event's end date and time.
     * @throws IllegalArgumentException if a field is null or the range is reversed.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        this(description, new DateTimeParser.ParsedDateTime(from, true),
                new DateTimeParser.ParsedDateTime(to, true));
    }

    /**
     * Creates an event from already parsed date/time values.
     */
    private Event(String description, DateTimeParser.ParsedDateTime from,
            DateTimeParser.ParsedDateTime to) {
        super(description);
        if (from == null || to == null) {
            throw new IllegalArgumentException("An event's start and end cannot be null.");
        }
        // An end supplied without a time includes that entire day, as with within-period tasks.
        LocalDateTime effectiveEnd = to.hasTime() ? to.value()
                : to.value().toLocalDate().atTime(LocalTime.MAX);
        if (from.value().isAfter(effectiveEnd)) {
            throw new IllegalArgumentException("The event's start must not be after its end.");
        }
        this.from = from.value();
        this.hasFromTime = from.hasTime();
        this.to = to.value();
        this.hasToTime = to.hasTime();
    }

    /**
     * Returns the event's start date and optional time.
     *
     * @return the event's start date and optional time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event's end date and optional time.
     *
     * @return the event's end date and optional time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns the event task type used for display and storage.
     */
    @Override
    protected TaskType getTaskType() {
        return TaskType.EVENT;
    }

    /**
     * Returns the description followed by the formatted event date range.
     *
     * @return the display text for this event.
     */
    @Override
    protected String getTaskDetails() {
        return super.getTaskDetails() + " (from: "
                + DateTimeParser.formatForDisplay(from, hasFromTime) + " to: "
                + DateTimeParser.formatForDisplay(to, hasToTime) + ")";
    }

    /**
     * Returns the base task fields followed by the stored event dates.
     *
     * @return fields used to serialize this event.
     */
    @Override
    protected List<String> getStorageFields() {
        List<String> fields = new ArrayList<>(super.getStorageFields());
        fields.add(DateTimeParser.formatForStorage(from, hasFromTime));
        fields.add(DateTimeParser.formatForStorage(to, hasToTime));
        return fields;
    }

    /**
     * Converts a date-only constructor argument into the shared parsed representation.
     */
    private static DateTimeParser.ParsedDateTime convertDateOnly(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("An event date cannot be null.");
        }
        return new DateTimeParser.ParsedDateTime(date.atStartOfDay(), false);
    }

    /** Parses an event boundary and provides a command-friendly explanation of invalid input. */
    private static DateTimeParser.ParsedDateTime parseDate(String text) {
        try {
            return DateTimeParser.parse(text);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Please use valid dates such as 2019-10-15 or 2/12/2019 1800.", exception);
        }
    }
}
