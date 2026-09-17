package glennon.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date and time.
 */
public class Deadline extends Task {
    /** Format used when showing deadlines to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu, h:mm a", Locale.ENGLISH);

    /** Date and time by which this task should be completed. */
    private final LocalDateTime dueDateTime;

    /**
     * Creates a pending deadline task with the given description and deadline.
     *
     * @param description description of the task.
     * @param dueDateTime date and time by which the task should be completed.
     * @throws IllegalArgumentException if the description is invalid or the date-time is null.
     */
    public Deadline(String description, LocalDateTime dueDateTime) {
        super(description);
        if (dueDateTime == null) {
            throw new IllegalArgumentException("Deadline date and time must not be null.");
        }
        this.dueDateTime = dueDateTime;
    }

    /**
     * Checks for the same concrete type, description, and deadline date-time,
     * ignoring completion status.
     *
     * @param other task to compare with this deadline.
     * @return true when both deadlines have the same defining details.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return other instanceof Deadline deadline
                && super.hasSameDetails(other)
                && dueDateTime.equals(deadline.dueDateTime);
    }

    /**
     * Returns the deadline date and time.
     *
     * @return deadline date and time.
     */
    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    /**
     * Checks whether this deadline is due on the given date.
     *
     * @param date date to check.
     * @return true when the deadline is due on that date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return dueDateTime.toLocalDate().equals(date);
    }

    /**
     * Returns the task with its type, completion status, and deadline.
     *
     * @return display-ready text such as
     *         {@code [D][ ] return book (by: Dec 2 2019, 6:00 PM)}.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDateTime.format(DISPLAY_FORMAT) + ")";
    }
}
