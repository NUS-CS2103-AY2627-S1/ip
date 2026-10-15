package anders.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/** Represents a task scheduled between a start time and an end time. */
public class Event extends Task {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter LEGACY_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT = DateTimeFormatter.ofPattern("h.mm a", Locale.ENGLISH);
    private static final List<DateTimeFormatter> DATE_TIME_FORMATS = List.of(
            DATE_TIME_FORMAT, LEGACY_DATE_TIME_FORMAT, INPUT_DATE_TIME_FORMAT);
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(DATE_FORMAT, INPUT_DATE_FORMAT);
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final boolean hasFromTime;
    private final boolean hasToTime;

    /**
     * Creates a new unfinished event task.
     *
     * @param description the task description
     * @param from the event start date/time in yyyy-MM-dd or yyyy-MM-dd HHmm format
     * @param to the event end date/time in yyyy-MM-dd or yyyy-MM-dd HHmm format
     * @throws DateTimeParseException if either endpoint is not a valid date/time
     * @throws IllegalArgumentException if the event ends before it starts
     */
    public Event(String description, String from, String to) {
        super(description);
        ParsedDateTime parsedFrom = parseDateTime(from);
        ParsedDateTime parsedTo = parseDateTime(to);
        this.from = parsedFrom.value;
        this.to = parsedTo.value;
        this.hasFromTime = parsedFrom.hasTime;
        this.hasToTime = parsedTo.hasTime;
        if (this.from.isAfter(this.to)) {
            throw new IllegalArgumentException("An event must end at or after it starts.");
        }
    }

    /** @return the event start date and time */
    public LocalDateTime getFrom() {
        return from;
    }

    /** @return the event end date and time */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns the display text for this event task. */
    @Override
    public String toString() {
        String fromText = formatForDisplay(from, hasFromTime);
        String toText = formatForDisplay(to, hasToTime);
        return "[E] " + super.toString() + " (from: " + fromText + " to: " + toText + ")"
                + getTagsDisplayText();
    }

    /** Returns the canonical start value used for persistence. */
    public String getFromText() {
        return from.format(hasFromTime ? DATE_TIME_FORMAT : DATE_FORMAT);
    }

    /** Returns the canonical end value used for persistence. */
    public String getToText() {
        return to.format(hasToTime ? DATE_TIME_FORMAT : DATE_FORMAT);
    }

    private static String formatForDisplay(LocalDateTime dateTime, boolean hasTime) {
        String date = dateTime.format(DISPLAY_DATE_FORMAT);
        String time = dateTime.format(DISPLAY_TIME_FORMAT).toLowerCase(Locale.ROOT);
        return date + (hasTime ? " " + time : "");
    }

    private static ParsedDateTime parseDateTime(String value) {
        DateTimeParseException lastError = new DateTimeParseException("Invalid date/time", value, 0);
        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                return new ParsedDateTime(LocalDateTime.parse(value, format), true);
            } catch (DateTimeParseException exception) {
                lastError = exception;
            }
        }
        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return new ParsedDateTime(LocalDate.parse(value, format).atStartOfDay(), false);
            } catch (DateTimeParseException exception) {
                lastError = exception;
            }
        }
        throw lastError;
    }

    /** Stores a parsed date/time and whether the input explicitly included a time. */
    private record ParsedDateTime(LocalDateTime value, boolean hasTime) {
    }
}
