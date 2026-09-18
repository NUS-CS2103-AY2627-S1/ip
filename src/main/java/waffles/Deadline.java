package waffles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** The date by which the task should be completed. */
    protected LocalDate by;

    /**
     * Creates a new deadline task.
     *
     * @param description the text describing the task
     * @param by the date by which the task should be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Creates a deadline from an ISO-8601 date such as {@code 2019-10-15}.
     *
     * @param description the text describing the task
     * @param by the ISO-8601 date by which the task should be completed
     */
    public Deadline(String description, String by) {
        this(description, LocalDate.parse(by));
    }

    /**
     * Returns the date by which this task should be completed.
     *
     * @return the deadline value
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns the deadline in the format used by the task list.
     *
     * @return the deadline type marker, status, description, and due time
     */
    @Override
    public String toString() {
        String formattedDate = by.format(DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH));
        return "[D]" + super.toString() + " (by: " + formattedDate + ")";
    }
}
