package min.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline with the given description and due date.
     *
     * @param description The deadline description. Must not be null, blank, or contain the
     *                    storage field separator.
     * @param dueDate The deadline's due date.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /** Returns this deadline in the format used for persistent storage. */
    @Override
    public String toFileString() {
        return "D" + FILE_FIELD_SEPARATOR + getStatusValue() + FILE_FIELD_SEPARATOR
                + getDescription() + FILE_FIELD_SEPARATOR + dueDate;
    }

    /** Returns this deadline in a format suitable for display. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDate.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
