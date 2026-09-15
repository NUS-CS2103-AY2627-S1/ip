package jaku.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a calendar date.
 */
public class Deadline extends Task {
    /** Formatter used for deadline dates shown in Jaku's user interface. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);

    /** Date by which the task should be completed. */
    private final LocalDate by;

    /**
     * Creates an incomplete deadline with the given description and due date.
     *
     * @param description the text describing the deadline
     * @param by the date by which it should be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline's due date without display formatting.
     *
     * @return the due date
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns this deadline with its task-type icon and formatted due date.
     *
     * @return the deadline formatted for display by Jaku
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DISPLAY_DATE_FORMAT.format(by) + ")";
    }
}
