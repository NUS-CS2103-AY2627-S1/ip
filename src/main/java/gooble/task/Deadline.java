package gooble.task;

import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specified deadline.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter STORAGE_TIME = DateTimeFormatter.ofPattern("HHmm");
    /** The typed deadline date and optional time associated with this task. */
    private final DeadlineDateParser.DeadlineDate deadline;

    /**
     * Creates an incomplete deadline task with a description and deadline.
     *
     * @param description the text describing the task
     * @param deadline the date or time by which the task should be completed
     */
    public Deadline(String description, DeadlineDateParser.DeadlineDate deadline) {
        super(description);
        if (deadline == null) {
            throw new IllegalArgumentException("Deadline cannot be null.");
        }
        this.deadline = deadline;
    }

    /**
     * Returns the deadline text.
     *
     * @return the deadline
     */
    public String getDeadline() {
        return DeadlineDateParser.format(deadline);
    }

    /** Returns the ISO-style value used when saving this deadline. */
    public String getStoredDeadline() {
        return deadline.time() == null ? deadline.date().toString()
                : deadline.date() + " " + deadline.time().format(STORAGE_TIME);
    }

    /**
     * Returns the formatted representation used for a deadline task.
     *
     * @return the task type, completion status, description, and deadline
     */
    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + description + formatTags()
                + " (by: " + getDeadline() + ")";
    }
}
