package larp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates an incomplete deadline with the specified description and date.
     *
     * @param description Description of the deadline.
     * @param by Date by which the deadline should be completed.
     */
    public Deadline(String description, LocalDate by) {
        this(description, by, false);
    }

    /**
     * Creates a deadline with its saved completion state.
     *
     * @param description Description of the deadline.
     * @param by Date by which the deadline should be completed.
     * @param isDone Whether the deadline has been completed.
     */
    public Deadline(String description, LocalDate by, boolean isDone) {
        super(description, isDone);
        this.by = by;
    }

    /**
     * Returns the date by which this task should be completed.
     *
     * @return Deadline date.
     */
    public LocalDate getBy() {
        return this.by;
    }

    /**
     * Returns a display-friendly representation of this deadline.
     *
     * @return Formatted deadline description, status, and date.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + this.by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
