package bob.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a given date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description description of the task
     * @param by date by which the task should be completed
     */
    public Deadline(String description, LocalDate by) {
        this(description, by, Priority.NONE);
    }

    /**
     * Creates an incomplete task with an explicit priority.
     *
     * @param description description of the task
     * @param by due date
     * @param priority priority tag, or NONE for no tag
     */
    public Deadline(String description, LocalDate by, Priority priority) {
        super(description, priority);
        this.by = by;
    }

    /**
     * Serializes this deadline with its task type and due date.
     *
     * @return one deadline record suitable for storage
     */
    @Override
    public String toStorageString() {
        return "D | " + super.toStorageString() + " | " + by + getStoragePriority();
    }

    /**
     * Formats this deadline for display with a friendly due date.
     *
     * @return display form of this deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
