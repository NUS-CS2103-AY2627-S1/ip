package unicorn.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Represents a task that must be completed by a date or date and time.
 */
public class DeadlineTask extends Task {
    private static final DateTimeFormatter DATE_INPUT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter EXAMPLE_DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME_OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH);

    private final LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description task description
     * @param by date and time by which the task is due
     */
    public DeadlineTask(String description, LocalDateTime by) {
        super(description);
        assert by != null : "Deadline date and time must not be null";

        this.by = by;
    }

    /**
     * Converts supported user input into a date and time. Date-only input is
     * represented as the start of that day.
     *
     * @param dateTimeText date entered after {@code /by}
     * @return parsed date and time
     * @throws DateTimeParseException if the input does not match a supported format
     */
    public static LocalDateTime parseBy(String dateTimeText) {
        try {
            return LocalDateTime.parse(dateTimeText, DATE_TIME_INPUT_FORMAT);
        } catch (DateTimeParseException firstFormatException) {
            try {
                return LocalDateTime.parse(dateTimeText, EXAMPLE_DATE_TIME_INPUT_FORMAT);
            } catch (DateTimeParseException exampleFormatException) {
                LocalDate date = LocalDate.parse(dateTimeText, DATE_INPUT_FORMAT);
                return date.atStartOfDay();
            }
        }
    }

    /**
     * Returns the deadline date and time.
     *
     * @return deadline date and time
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Reports whether another deadline has the same description and due date.
     *
     * @param other task to compare
     * @return {@code true} when both tasks represent the same deadline
     */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other)
                && by.equals(((DeadlineTask) other).by);
    }

    /**
     * Returns a display representation of this deadline task.
     *
     * @return task status, description, and formatted deadline
     */
    @Override
    public String toString() {
        return "[D] [" + getStatusIcon() + "] "
                + description + " (by: " + formatBy() + ")";
    }

    /**
     * Formats a deadline for the task list.
     *
     * @return formatted deadline
     */
    private String formatBy() {
        if (by.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return by.format(DATE_OUTPUT_FORMAT);
        }
        return by.format(DATE_TIME_OUTPUT_FORMAT);
    }
}
