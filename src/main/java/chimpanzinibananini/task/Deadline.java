package chimpanzinibananini.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

/**
 * Represents a task that must be completed by a particular date and time.
 */
public class Deadline extends Task {
    private static final List<DateTimeFormatter> DATE_TIME_INPUT_FORMATS = List.of(
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    private static final DateTimeFormatter DATE_INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter DATE_TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mma");

    private final LocalDateTime by;

    /**
     * Creates an incomplete deadline by parsing the specified date and time.
     */
    public Deadline(String description, String by) {
        this(description, parseDateTime(by));
    }

    /**
     * Creates an incomplete deadline with the specified date and time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Parses a supported user or storage date. A date without a time is treated
     * as midnight at the start of that date.
     */
    public static LocalDateTime parseDateTime(String value) {
        for (DateTimeFormatter formatter : DATE_TIME_INPUT_FORMATS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next documented input format.
            }
        }
        try {
            return LocalDate.parse(value, DATE_INPUT_FORMAT).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException(
                    "Use yyyy-MM-dd or d/M/yyyy HHmm (for example, 2/12/2019 1800)",
                    value, e.getErrorIndex(), e);
        }
    }

    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public String getTypeIcon() {
        return "D";
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | " + by.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public String toString() {
        DateTimeFormatter displayFormat = by.toLocalTime().equals(java.time.LocalTime.MIDNIGHT)
                ? DATE_DISPLAY_FORMAT : DATE_TIME_DISPLAY_FORMAT;
        return super.toString() + " (by: " + by.format(displayFormat) + ")";
    }
}
