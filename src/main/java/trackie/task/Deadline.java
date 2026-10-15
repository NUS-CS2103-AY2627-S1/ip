package trackie.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates an incomplete deadline with the given description and due time.
     *
     * @param description description of the deadline
     * @param by date by which the task should be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns the deadline with its type, status, and due time.
     *
     * @return formatted deadline
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + this.by.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns a data-file line containing this deadline's due time.
     *
     * @return serialized deadline
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + this.by;
    }
}
