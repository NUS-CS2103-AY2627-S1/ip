package kibo.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    /** Date by which the task must be completed. */
    private final LocalDate by;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param description description of the task.
     * @param by deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "A deadline must have a due date";

        this.by = by;
    }

    /**
     * Returns the deadline date for storage.
     *
     * @return deadline date.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns whether this deadline is due on the supplied date.
     *
     * @param date date whose schedule is being viewed.
     * @return {@code true} when the deadline is due on the date.
     */
    @Override
    public boolean isScheduledOn(LocalDate date) {
        assert date != null : "A schedule date must not be null";
        return by.equals(date);
    }

    /**
     * Returns this task in the deadline display format.
     *
     * @return task text containing the {@code [D]} type marker and formatted deadline date.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
