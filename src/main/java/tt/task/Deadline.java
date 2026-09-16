package tt.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a particular calendar date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline for the supplied date.
     *
     * @param description description of the deadline.
     * @param dueDate date by which the task should be completed.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /**
     * Creates a deadline from an ISO date for convenient text-based callers.
     *
     * @param description description of the deadline.
     * @param dueDate due date in ISO-8601 format.
     */
    public Deadline(String description, String dueDate) {
        this(description, LocalDate.parse(dueDate.trim()));
    }

    /**
     * Returns the deadline as a date suitable for comparisons and calculations.
     *
     * @return due date of the deadline.
     */
    public LocalDate getBy() {
        return dueDate;
    }

    /**
     * Returns the date in the format shown to the user.
     *
     * @return formatted due date.
     */
    public String getFormattedBy() {
        return dueDate.format(DISPLAY_FORMAT);
    }

    /**
     * Returns the deadline with its type marker, status, and formatted date.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + getFormattedBy() + ")";
    }
}
