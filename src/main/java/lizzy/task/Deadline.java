package lizzy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    /**
     * The format used to present parsed dates to the user.
     */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /**
     * The deadline date.
     */
    protected final LocalDate by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description the text describing the deadline
     * @param by the deadline date
     */
    public Deadline(String description, LocalDate by) {
        this(description, by, false);
    }

    /**
     * Restores a deadline with its saved completion state.
     *
     * @param description the text describing the deadline
     * @param by the deadline date
     * @param isDone whether the deadline has been completed
     */
    public Deadline(String description, LocalDate by, boolean isDone) {
        super(description, isDone);
        if (by == null) {
            throw new IllegalArgumentException("A deadline date cannot be null.");
        }
        this.by = by;
    }

    /**
     * Returns whether another task is a deadline with the same description and date.
     *
     * @param other the task to compare
     * @return {@code true} if both deadlines have the same details
     */
    @Override
    boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && by.equals(((Deadline) other).by);
    }

    /**
     * Returns whether this deadline falls on the given date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    /**
     * Returns this task's deadline date.
     *
     * @return the deadline date
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns a display-ready representation of this deadline.
     *
     * @return the deadline type marker, completion status, description, and formatted deadline date.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
