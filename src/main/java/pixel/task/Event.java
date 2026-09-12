package pixel.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a task that occurs over a specified period.
 */
public class Event extends Task {
    private static final DateTimeFormatter SPACE_SEPARATED_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TWELVE_HOUR_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("h[:mm]a")
            .toFormatter(Locale.ENGLISH);
    private static final LocalDate TIME_ONLY_COMPARISON_DATE = LocalDate.of(1970, 1, 1);

    /** Start time of this event. */
    protected String from;
    /** End time of this event. */
    protected String to;

    /**
     * Creates an event with the specified description and time range.
     *
     * @param description User-visible task description.
     * @param from Event start time.
     * @param to Event end time.
     */
    public Event(String description, String from, String to) {
        super(description);
        validateChronologicalOrder(from, to);
        this.from = from;
        this.to = to;
    }

    @Override
    public Task withDescription(String newDescription) {
        return preserveStatus(new Event(newDescription, from, to));
    }

    /**
     * Returns a copy of this event with a new start time.
     *
     * @param newStart Replacement start time.
     * @return Updated event with the same remaining details and completion status.
     */
    public Event withStart(String newStart) {
        return preserveStatus(new Event(getDescription(), newStart, to));
    }

    /**
     * Returns a copy of this event with a new end time.
     *
     * @param newEnd Replacement end time.
     * @return Updated event with the same remaining details and completion status.
     */
    public Event withEnd(String newEnd) {
        return preserveStatus(new Event(getDescription(), from, newEnd));
    }

    @Override
    public boolean occursOn(LocalDate date) {
        try {
            LocalDate startDate = parseLeadingDate(from);
            LocalDate endDate;
            try {
                endDate = parseLeadingDate(to);
            } catch (DateTimeParseException exception) {
                endDate = startDate;
            }
            return !date.isBefore(startDate) && !date.isAfter(endDate);
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && from.equals(((Event) other).from)
                && to.equals(((Event) other).to);
    }

    private static void validateChronologicalOrder(String from, String to) {
        LocalDateTime start = parseStructuredDateTime(from);
        LocalDateTime end = parseStructuredDateTime(to);
        if (start != null && end != null && !start.isBefore(end)) {
            throw new IllegalArgumentException(
                    "Oops! The event start must be before the event end.");
        }
    }

    private static LocalDateTime parseStructuredDateTime(String value) {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException exception) {
            try {
                return LocalDateTime.parse(value, SPACE_SEPARATED_DATE_TIME);
            } catch (DateTimeParseException nestedException) {
                try {
                    return LocalDate.parse(value).atStartOfDay();
                } catch (DateTimeParseException ignoredException) {
                    return parseStructuredTime(value);
                }
            }
        }
    }

    private static LocalDateTime parseStructuredTime(String value) {
        try {
            return TIME_ONLY_COMPARISON_DATE.atTime(LocalTime.parse(value));
        } catch (DateTimeParseException exception) {
            try {
                return TIME_ONLY_COMPARISON_DATE.atTime(LocalTime.parse(value, TWELVE_HOUR_TIME));
            } catch (DateTimeParseException ignoredException) {
                return null;
            }
        }
    }

    /**
     * Parses an ISO date from the beginning of an Event time string.
     * This accepts values such as {@code 2019-12-02 14:00} and
     * {@code 2019-12-02T14:00} while preserving the existing free-form syntax.
     *
     * @param dateTime Event time text.
     * @return Date at the beginning of the text.
     * @throws DateTimeParseException If no valid ISO date begins the text.
     */
    private static LocalDate parseLeadingDate(String dateTime) {
        if (dateTime.length() < 10) {
            return LocalDate.parse(dateTime);
        }
        return LocalDate.parse(dateTime.substring(0, 10));
    }

    @Override
    public String toDataString() {
        return "E | " + (isDone() ? "1" : "0") + " | " + escapeDataField(getDescription())
                + " | " + escapeDataField(from) + " | " + escapeDataField(to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from + " to: " + to + ")";
    }
}
