package quackie.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * A task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final String DATE_TIME_PATTERN = "\\d{1,2}/\\d{1,2}/\\d{4} \\d{4}";

    /** The start value exactly as entered; saved to storage and used for duplicate checks. */
    private final String from;
    /** The end value exactly as entered; saved to storage and used for duplicate checks. */
    private final String to;
    /** The parsed start time, or {@code null} when the event uses free-form times. */
    private final LocalDateTime startTime;
    /** The parsed end time, or {@code null} when the event uses free-form times. */
    private final LocalDateTime endTime;

    /**
     * Creates a not-done event task.
     *
     * @param description the text describing the event
     * @param from the event start date or time
     * @param to the event end date or time
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
        LocalDateTime[] parsedTimes = parseTimeRange(from, to);
        this.startTime = parsedTimes[0];
        this.endTime = parsedTimes[1];
    }

    @Override
    protected String getTypeIcon() {
        return "[E]";
    }

    /**
     * Returns the event's starting date or time.
     *
     * @return the event start value
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's ending date or time.
     *
     * @return the event end value
     */
    public String getTo() {
        return to;
    }

    @Override
    boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && from.equalsIgnoreCase(((Event) other).from)
                && to.equalsIgnoreCase(((Event) other).to);
    }

    /**
     * Parses and validates structured event times while retaining support for free-form values.
     *
     * @param from the event start value entered by the user
     * @param to the event end value entered by the user
     * @return the parsed start and end times, or two {@code null}s when both values are free-form
     * @throws IllegalArgumentException if only one value is structured, a value is not a real date-time,
     *         or the end time is not after the start time
     */
    private static LocalDateTime[] parseTimeRange(String from, String to) {
        boolean hasStructuredStart = from.matches(DATE_TIME_PATTERN);
        boolean hasStructuredEnd = to.matches(DATE_TIME_PATTERN);
        if (!hasStructuredStart && !hasStructuredEnd) {
            return new LocalDateTime[] {null, null};
        }
        if (!hasStructuredStart || !hasStructuredEnd) {
            throw new IllegalArgumentException(
                    "Both event times must use d/M/yyyy HHmm when either one does.");
        }

        try {
            LocalDateTime startTime = LocalDateTime.parse(from, DATE_TIME_FORMAT);
            LocalDateTime endTime = LocalDateTime.parse(to, DATE_TIME_FORMAT);
            if (!startTime.isBefore(endTime)) {
                throw new IllegalArgumentException("The event end time must be after its start time.");
            }
            return new LocalDateTime[] {startTime, endTime};
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Please enter valid event times using d/M/yyyy HHmm.");
        }
    }

    /**
     * Formats a parsed time for display, or returns the original free-form value unchanged.
     *
     * @param rawValue the value as entered by the user
     * @param parsedValue the parsed time, or {@code null} for a free-form value
     * @return the user-facing event time
     */
    private static String formatForDisplay(String rawValue, LocalDateTime parsedValue) {
        return parsedValue == null ? rawValue : DISPLAY_DATE_TIME_FORMAT.format(parsedValue);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + formatForDisplay(from, startTime)
                + " to: " + formatForDisplay(to, endTime) + ")";
    }
}
