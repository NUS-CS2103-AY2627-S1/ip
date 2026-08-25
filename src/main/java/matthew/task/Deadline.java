package matthew.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a task that must be completed by a specified date and time. */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    /** Date and time by which the task should be completed. */
    protected LocalDateTime by;

    /**
     * Creates an incomplete deadline with the specified description and due date/time.
     *
     * @param description Description of the deadline.
     * @param by Date and time by which the deadline should be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the date and time by which the deadline should be completed.
     *
     * @return Deadline date and time.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns whether this deadline falls on the specified date.
     *
     * @param date Date to check.
     * @return {@code true} if the deadline falls on the date.
     */
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /** Returns the deadline formatted with its due date/time and status. */
    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
