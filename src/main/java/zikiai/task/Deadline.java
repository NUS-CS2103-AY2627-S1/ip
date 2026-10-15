package zikiai.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a deadline.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate deadline;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description description of the task.
     * @param deadline deadline date for the task.
     */
    public Deadline(String description, LocalDate deadline) {
        super(description);
        assert deadline != null : "Deadline date must not be null";
        this.deadline = deadline;
    }

    /**
     * Returns this deadline with its type, completion status, and due date.
     *
     * @return formatted deadline for display
     */
    @Override
    public String getDescription() {
        return "[D]" + super.getDescription()
                + " (by: " + deadline.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns this deadline in the format used by the data file.
     *
     * @return storage representation of this deadline
     */
    @Override
    public String toDataString() {
        return "[D][" + getStatusIcon() + "]" + getPriorityStorageTag()
                + " | " + getDescriptionText() + " | " + deadline;
    }
}
