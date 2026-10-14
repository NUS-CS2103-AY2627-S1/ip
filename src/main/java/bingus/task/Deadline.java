package bingus.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified deadline.
 */
public class Deadline extends Task {

    /** Formatter used to present a deadline in task output. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu, h:mm a", Locale.ENGLISH);

    /** Date and time by which this task must be completed. */
    private final LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description task description.
     * @param by date and time by which the task is due.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);

        assert by != null : "Deadline date and time must not be null";

        this.by = by;
    }

    /**
     * Returns the date and time by which this task is due.
     *
     * @return deadline date and time.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns whether this deadline is due on the specified date.
     *
     * @param date date to check.
     * @return true if this deadline is due on the date.
     */
    @Override
    public boolean matchesDate(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /**
     * Returns this deadline in the format used for display.
     *
     * @return formatted deadline task text.
     */
    @Override
    public String getDisplayString() {
        return super.getDisplayString() + " (by: " + by.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }
}
