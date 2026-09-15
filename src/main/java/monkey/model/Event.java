package monkey.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Pattern;

/** Represents a task that starts and ends at stated date/time values. */
public class Event extends Task {
    private static final String INVALID_TIME_MESSAGE =
            "Use event times like yyyy-mm-dd HHmm, d/M/yyyy HHmm, or h:mm am/pm.";
    private static final Pattern DATE_TIME_SHAPE = Pattern.compile(
            "(?:\\d{4}-\\d{1,2}-\\d{1,2}|\\d{1,2}/\\d{1,2}/\\d{4}) \\d{4}");
    private static final Pattern DATE_SHAPE = Pattern.compile(
            "(?:\\d{4}-\\d{1,2}-\\d{1,2}|\\d{1,2}/\\d{1,2}/\\d{4})");
    private static final Pattern TIME_SHAPE = Pattern.compile("(?i)\\d{1,2}(?::\\d{2})?\\s*(?:am|pm)|\\d{4}");
    private static final DateTimeFormatter[] DATE_TIME_FORMATS = {
        DateTimeFormatter.ofPattern("uuuu-M-d HHmm").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT)
    };
    private static final DateTimeFormatter[] TIME_FORMATS = {
        new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("h:mma").toFormatter(Locale.ENGLISH),
        new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("ha").toFormatter(Locale.ENGLISH),
        DateTimeFormatter.ofPattern("HHmm")
    };
    private static final DateTimeFormatter[] DATE_FORMATS = {
        DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT)
    };

    private final String from;
    private final String to;

    /** Creates an incomplete event with its start and end date/time values. */
    public Event(String description, String from, String to) {
        super(description);
        this.from = normalizeTime(from);
        this.to = normalizeTime(to);
        validateChronology(this.from, this.to);
    }

    /** Returns the event start time for persistence. */
    public String getFrom() {
        return from;
    }

    /** Returns the event end time for persistence. */
    public String getTo() {
        return to;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        if (!super.hasSameDetails(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return from.equalsIgnoreCase(otherEvent.from) && to.equalsIgnoreCase(otherEvent.to);
    }

    private static String normalizeTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("An event needs both a start and an end time.");
        }
        if (value.contains("|")) {
            throw new IllegalArgumentException("Event times cannot contain '|'.");
        }
        return value.trim().replaceAll("\\s+", " ");
    }

    private static void validateChronology(String from, String to) {
        LocalDateTime fromDateTime = parseDateTime(from);
        LocalDateTime toDateTime = parseDateTime(to);
        if (fromDateTime != null || toDateTime != null) {
            if (fromDateTime == null || toDateTime == null || !toDateTime.isAfter(fromDateTime)) {
                throw new IllegalArgumentException("An event must end after it starts.");
            }
            return;
        }

        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);
        if (fromDate != null || toDate != null) {
            if (fromDate == null || toDate == null || !toDate.isAfter(fromDate)) {
                throw new IllegalArgumentException("An event must end after it starts.");
            }
            return;
        }

        LocalTime fromTime = parseTrailingTime(from);
        LocalTime toTime = parseTrailingTime(to);
        if (fromTime != null && toTime != null && !toTime.isAfter(fromTime)) {
            throw new IllegalArgumentException("An event must end after it starts.");
        }
    }

    private static LocalDate parseDate(String value) {
        if (!DATE_SHAPE.matcher(value).matches()) {
            return null;
        }
        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return LocalDate.parse(value, format);
            } catch (DateTimeParseException ignored) {
                // Try the next supported date format.
            }
        }
        throw new IllegalArgumentException(INVALID_TIME_MESSAGE);
    }

    private static LocalDateTime parseDateTime(String value) {
        if (!DATE_TIME_SHAPE.matcher(value).matches()) {
            return null;
        }
        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(value, format);
            } catch (DateTimeParseException ignored) {
                // Try the next supported date-time format.
            }
        }
        throw new IllegalArgumentException(INVALID_TIME_MESSAGE);
    }

    private static LocalTime parseTrailingTime(String value) {
        String[] words = value.split(" ");
        String candidate = words[words.length - 1];
        if (!TIME_SHAPE.matcher(candidate).matches()) {
            return null;
        }
        for (DateTimeFormatter format : TIME_FORMATS) {
            try {
                return LocalTime.parse(candidate.replace(" ", ""), format);
            } catch (DateTimeParseException ignored) {
                // Try the next supported time format.
            }
        }
        throw new IllegalArgumentException(INVALID_TIME_MESSAGE);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
