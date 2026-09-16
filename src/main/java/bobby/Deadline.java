package bobby;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Represents a task with a typed date and optional time deadline. */
public class Deadline extends Task {
    /** Strict parser for date-only deadline input. */
    private static final DateTimeFormatter DATE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);
    /** Strict parser for deadline input containing a date and time. */
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    /** Formatter for displaying the date portion of a deadline. */
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    /** Formatter for displaying the time portion of a deadline. */
    private static final DateTimeFormatter TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    /** Parsed deadline value, using midnight for date-only input. */
    private final LocalDateTime dueDateTime;
    /** Whether the original input included an explicit time. */
    private final boolean hasExplicitTime;

    /**
     * Creates an incomplete deadline task for a date without a time.
     *
     * @param description text describing the task
     * @param dueDate date by which the task should be completed
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDateTime = dueDate.atStartOfDay();
        this.hasExplicitTime = false;
    }

    /**
     * Creates an incomplete deadline task for a date and time.
     *
     * @param description text describing the task
     * @param dueDateTime date and time by which the task should be completed
     */
    public Deadline(String description, LocalDateTime dueDateTime) {
        super(description);
        this.dueDateTime = dueDateTime;
        this.hasExplicitTime = true;
    }

    /**
     * Creates a deadline by parsing one of Bobby's supported input formats.
     *
     * @param description text describing the task
     * @param input date in {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} format
     * @return a deadline containing the parsed date and optional time
     * @throws DateTimeParseException if {@code input} is not a supported date
     *         or date-time value
     */
    public static Deadline fromInput(String description, String input)
            throws DateTimeParseException {
        if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return new Deadline(description, LocalDate.parse(input, DATE_INPUT_FORMAT));
        }
        return new Deadline(description, LocalDateTime.parse(input, DATE_TIME_INPUT_FORMAT));
    }

    /**
     * Returns the deadline as a typed date and time.
     *
     * @return the deadline, using midnight when the input contained only a date
     */
    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    /** Returns whether another task has the same type, description, deadline, and time precision. */
    @Override
    public boolean hasSameDetails(Task other) {
        if (!(other instanceof Deadline otherDeadline)) {
            return false;
        }
        return super.hasSameDetails(other)
                && dueDateTime.equals(otherDeadline.dueDateTime)
                && hasExplicitTime == otherDeadline.hasExplicitTime;
    }

    /**
     * Returns the date in the display format, including a time when one was supplied.
     *
     * @return formatted deadline text for display
     */
    private String getDisplayDate() {
        String date = dueDateTime.format(DATE_DISPLAY_FORMAT);
        return hasExplicitTime ? date + " " + dueDateTime.format(TIME_DISPLAY_FORMAT) : date;
    }

    /**
     * Returns the date in the canonical format used when saving tasks to disk.
     *
     * @return formatted deadline text for storage
     */
    private String getStorageDate() {
        return hasExplicitTime
                ? dueDateTime.format(DATE_TIME_INPUT_FORMAT)
                : dueDateTime.toLocalDate().format(DATE_INPUT_FORMAT);
    }

    /** Returns this deadline task in the format used by the command line interface. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + getDisplayDate() + ")";
    }

    /** Returns this deadline in the format used when saving tasks to disk. */
    @Override
    public String toStorageString() {
        return "D | " + getStorageStatus() + " | " + getDescription() + " | "
                + getStorageDate() + " | " + getPriority();
    }
}
