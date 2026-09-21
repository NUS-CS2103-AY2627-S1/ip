package yqr.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description description of the task.
     * @param by deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = Objects.requireNonNull(by, "Deadline date cannot be null");
    }

    /**
     * Returns the deadline details.
     *
     * @return deadline date.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns whether another deadline has the same description and date.
     *
     * @param other task to compare.
     * @return {@code true} if both deadlines have the same identifying details.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && by.equals(((Deadline) other).by);
    }

    /**
     * Returns a displayable version of this deadline task.
     *
     * @return deadline type icon, task details, and deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
