package oreo.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    /** The date or time by which this task should be completed. */
    private final LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description text describing the task
     * @param by date or time by which the task should be completed
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDate.parse(by);
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    @Override
    protected String getDetails() {
        return " (by: " + this.by.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + ")";
    }

    @Override
    protected String toStorageString() {
        return this.getTypeIcon() + " | " + this.getStatusIcon() + " | " + this.getDescription() + " | "
                + this.by + getTagsStorageRepresentation();
    }
}
