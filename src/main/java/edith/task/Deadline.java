package edith.task;

import java.time.LocalDateTime;

import edith.util.DateFormatter;

/** Represents a task that must be completed by a specified date, optionally with a time. */
public class Deadline extends Task {
    /** The date and optional time by which this task must be completed. */
    private final LocalDateTime by;
    /** Whether the user supplied a time for this deadline. */
    private final boolean hasTime;

    /**
     * Creates a deadline with its description and due date-time.
     *
     * @param description the text describing the task
     * @param by the due date-time
     * @param hasTime whether the deadline includes a time
     */
    public Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(description);
        this.by = by;
        this.hasTime = hasTime;
    }

    /**
     * Returns this deadline in the format used by the task list.
     *
     * @return the deadline type, status icon, description, and due date-time
     */
    @Override
    public String toString() {
        return toStorageString() + getTagSuffix();
    }

    @Override
    public String toStorageString() {
        return "[D]" + getStatusIcon() + " " + getDescription()
                + " (by: " + DateFormatter.formatForDisplay(by, hasTime) + ")";
    }

    /**
     * Returns the identifier for a deadline task.
     *
     * @return {@code deadline}
     */
    @Override
    protected String getTaskType() {
        return "deadline";
    }
}
