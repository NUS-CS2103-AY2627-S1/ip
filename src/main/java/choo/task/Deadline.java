package choo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Represents a task that must be completed by a given date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter
            .ofPattern("MMM d uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("MMM d uuuu, h:mma", Locale.ENGLISH);

    private final LocalDateTime dueDateTime;
    private final boolean hasExplicitTime;

    /**
     * Creates an incomplete Deadline with its due date or time.
     *
     * @param description description of the task.
     * @param dueDateTimeText Due date in {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} format.
     * @throws DateTimeParseException if the date or time is invalid.
     */
    public Deadline(String description, String dueDateTimeText) {
        super(description);
        this.hasExplicitTime = dueDateTimeText.contains(" ");
        this.dueDateTime = parseDueDateTime(dueDateTimeText);
    }

    /**
     * Returns the deadline date and time.
     *
     * @return deadline date and time.
     */
    public LocalDateTime getDueDateTime() {
        return this.dueDateTime;
    }

    /**
     * Returns whether the user supplied an explicit time for this deadline.
     *
     * @return true if the deadline includes a time.
     */
    public boolean hasExplicitTime() {
        return this.hasExplicitTime;
    }

    /**
     * Returns this task with its Deadline type marker and due text.
     *
     * @return formatted Deadline.
     */
    @Override
    public String toString() {
        DateTimeFormatter displayFormat = this.hasExplicitTime
                ? DISPLAY_DATE_TIME_FORMAT : DISPLAY_DATE_FORMAT;
        return "[D]" + super.toString()
                + " (by: " + this.dueDateTime.format(displayFormat) + ")";
    }

    /**
     * Parses a strict deadline date or date-time, using midnight for date-only input.
     *
     * @param dueDateTimeText Deadline in {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} format.
     * @return Parsed deadline date and time.
     * @throws DateTimeParseException If the syntax or date-time value is invalid.
     */
    private static LocalDateTime parseDueDateTime(String dueDateTimeText) {
        if (!dueDateTimeText.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}( [0-9]{4})?")) {
            throw new DateTimeParseException("Invalid deadline format", dueDateTimeText, 0);
        }
        if (dueDateTimeText.contains(" ")) {
            return LocalDateTime.parse(dueDateTimeText, INPUT_DATE_TIME_FORMAT);
        }
        return LocalDate.parse(dueDateTimeText, INPUT_DATE_FORMAT).atStartOfDay();
    }
}
