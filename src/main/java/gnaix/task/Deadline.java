package gnaix.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private LocalDate doBy;

    /**
     * Creates a deadline task with the given description and due date.
     *
     * @param description Description of the deadline.
     * @param doBy Date by which the task should be completed.
     */
    public Deadline(String description, LocalDate doBy) {
        super(description);
        this.doBy = doBy;
    }

    public LocalDate getDoBy() {
        return this.doBy;
    }

    /**
     * Returns whether the deadline occurs on the specified date.
     *
     * @param date Date to check.
     * @return True if the deadline falls on the specified date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return this.doBy.equals(date);
    }

    /**
     * Returns the deadline task with its due date.
     *
     * @return String representation of the deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + this.doBy.format(OUTPUT_DATE_FORMAT) + ")";
    }
}
