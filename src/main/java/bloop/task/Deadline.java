package bloop.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu");
    protected LocalDate by;

    /**
     * Creates a deadline with a description and due time.
     *
     * @param description description of the task
     * @param by due date
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /** Creates a deadline with the given description, due date, and priority. */
    public Deadline(String description, LocalDate by, Priority priority) {
        super(description, priority);
        this.by = by;
    }

    /**
     * Returns the deadline's due date.
     *
     * @return due date
     */
    public LocalDate getBy() {
        return by;
    }

    /** Returns whether this deadline is due on the supplied date. */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    /** Returns a display representation containing the formatted due date. */
    @Override
    public String toString() {
        return appendPriority("[D][" + getStatusIcon() + "] " + description
                + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")");
    }
}
