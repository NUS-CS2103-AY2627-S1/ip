package janet.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Represents a strictly formatted date, time, or date-time used by a task.
 */
public final class TaskDateTime {
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final Pattern TIME_PATTERN = Pattern.compile("\\d{2}:\\d{2}");
    private static final Pattern DATE_TIME_PATTERN =
            Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");

    private final DateTimeKind kind;
    private final LocalDateTime value;

    /**
     * Creates a comparable task date or time.
     *
     * @param kind type of temporal value.
     * @param value value on a common comparison timeline.
     */
    private TaskDateTime(DateTimeKind kind, LocalDateTime value) {
        this.kind = kind;
        this.value = value;
    }

    /**
     * Parses a strict date, time, or date-time.
     *
     * @param text text in {@code yyyy-MM-dd}, {@code HH:mm}, or {@code yyyy-MM-dd HH:mm} format.
     * @return parsed date or time
     * @throws DateTimeParseException if the text or represented value is invalid
     */
    public static TaskDateTime parse(String text) {
        if (DATE_TIME_PATTERN.matcher(text).matches()) {
            LocalDate date = LocalDate.parse(text.substring(0, 10));
            LocalTime time = LocalTime.parse(text.substring(11));
            return new TaskDateTime(DateTimeKind.DATE_TIME, date.atTime(time));
        }
        if (DATE_PATTERN.matcher(text).matches()) {
            return new TaskDateTime(DateTimeKind.DATE, LocalDate.parse(text).atStartOfDay());
        }
        if (TIME_PATTERN.matcher(text).matches()) {
            return new TaskDateTime(DateTimeKind.TIME,
                    LocalDate.of(1970, 1, 1).atTime(LocalTime.parse(text)));
        }
        throw new DateTimeParseException("Unsupported task date/time format", text, 0);
    }

    /**
     * Returns whether this value uses the same format category as another value.
     *
     * @param other value to compare.
     * @return whether both values are dates, times, or date-times
     */
    public boolean hasSameKind(TaskDateTime other) {
        return kind == other.kind;
    }

    /**
     * Returns whether this value precedes another value of the same kind.
     *
     * @param other value to compare.
     * @return whether this value is earlier
     */
    public boolean isBefore(TaskDateTime other) {
        assert hasSameKind(other) : "Only task date/time values of the same kind may be compared";
        return value.isBefore(other.value);
    }

    /**
     * Returns whether this value has passed according to the system clock.
     *
     * @return whether the represented date or time has passed
     */
    public boolean isPast() {
        return switch (kind) {
            case DATE -> value.toLocalDate().isBefore(LocalDate.now());
            case TIME -> value.toLocalTime().isBefore(LocalTime.now());
            case DATE_TIME -> value.isBefore(LocalDateTime.now());
        };
    }

    /** Types of structured temporal values accepted for tasks. */
    private enum DateTimeKind {
        DATE,
        TIME,
        DATE_TIME
    }
}
