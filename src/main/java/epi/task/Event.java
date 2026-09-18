
package epi.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

/** Represents a task with a start and end date/time. */
public class Event extends Task {
    private static final DateTimeFormatter INPUT_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HHmm").parseDefaulting(ChronoField.ERA, 1).toFormatter(Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");
    /** Date and time at which the event starts. */
    protected LocalDateTime from;
    /** Date and time at which the event ends, strictly after its start. */
    protected LocalDateTime to;

    /**
     * Creates an incomplete event by strictly parsing its start and end date/time values.
     *
     * @param description Task description, preserved without trimming.
     * @param fromString Non-null start date/time in {@code yyyy-MM-dd HHmm} format.
     * @param toString Non-null end date/time in {@code yyyy-MM-dd HHmm} format.
     * @throws DateTimeParseException If either date/time has an invalid format or value.
     * @throws IllegalArgumentException If the description is null, blank, or contains a pipe character,
     *     newline, or carriage return, or if the end is not strictly after the start.
     */
    public Event(String description, String fromString, String toString) throws DateTimeParseException {
        super(description);
        this.from = LocalDateTime.parse(fromString, INPUT_FORMAT);
        this.to = LocalDateTime.parse(toString, INPUT_FORMAT);
        if (!this.to.isAfter(this.from)) {
            throw new IllegalArgumentException("Meow! An event must end after it starts.");
        }
    }

    /**
     * Copies an event while sharing its immutable date/time values.
     *
     * @param original Non-null event to copy.
     */
    private Event(Event original) {
        super(original);
        this.from = original.from;
        this.to = original.to;
    }

    /**
     * Returns an independent event snapshot; its date/time values are immutable.
     *
     * @return A new event with the same description, completion state, and start and end date/times.
     */
    @Override
    public Event copy() {
        return new Event(this);
    }

    /**
     * Returns the event's start date/time for chronological sorting.
     *
     * @return The start date/time, not the end date/time.
     */
    @Override
    public LocalDateTime getSortDate() {
        return from;
    }

    /**
     * Returns the user-facing event representation.
     *
     * @return The task type, status, description, and start and end times in {@code MMM dd yyyy, h:mm a} format.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from.format(OUTPUT_FORMAT)
                + " to: " + to.format(OUTPUT_FORMAT) + ")";
    }

    /**
     * Returns the serialized event representation.
     *
     * @return An {@code E} record containing status, description, and both times in {@code yyyy-MM-dd HHmm} format.
     */
    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from.format(INPUT_FORMAT) + " | " + to.format(INPUT_FORMAT);
    }
}
