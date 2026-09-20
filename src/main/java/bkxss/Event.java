package bkxss;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Optional;

/**
 * A task that occurs during a specified time period.
 */
public class Event extends Task {
    private static final DateTimeFormatter SCHEDULE_DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("uuuu-MM-dd HHmm")
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description text that describes the event
     * @param from event start time supplied by the user
     * @param to event end time supplied by the user
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns the event start time for persistence. */
    public String getFrom() {
        return from;
    }

    /** Returns the event end time for persistence. */
    public String getTo() {
        return to;
    }

    /**
     * Returns the event start as a date and time when it uses the supported schedule format.
     *
     * @return the parsed start, or an empty value for a legacy free-text start
     */
    public Optional<LocalDateTime> getFromDateTime() {
        return parseDateTime(from);
    }

    /**
     * Returns the event end as a date and time when it uses the supported schedule format.
     *
     * @return the parsed end, or an empty value for a legacy free-text end
     */
    public Optional<LocalDateTime> getToDateTime() {
        return parseDateTime(to);
    }

    /**
     * Returns whether both event boundaries are parseable and form a positive time period.
     *
     * @return {@code true} if this event can be used for schedule calculations
     */
    public boolean hasScheduledTimes() {
        Optional<LocalDateTime> parsedFrom = getFromDateTime();
        Optional<LocalDateTime> parsedTo = getToDateTime();
        return parsedFrom.isPresent() && parsedTo.isPresent()
                && parsedFrom.get().isBefore(parsedTo.get());
    }

    /** Parses a date-time without preventing older free-text events from loading. */
    private static Optional<LocalDateTime> parseDateTime(String text) {
        try {
            return Optional.of(LocalDateTime.parse(text.trim(), SCHEDULE_DATE_FORMAT));
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }

    /** Returns the display text for this event task. */
    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + description
                + " (from: " + from + " to: " + to + ")";
    }
}
