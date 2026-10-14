package verity.task;

import java.time.format.DateTimeFormatter;

/**
 * Represents a task that the user wants to keep track of.
 * Each task has a description and a completion status.
 */
public class Task {
    /** Format dates are shown to the user in, e.g. "Oct 15 2019". */
    public static final DateTimeFormatter DATE_DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /** Description of this task. */
    protected String description;
    /** Whether this task has been marked as done. */
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     * The task is not done by default.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the description of this task.
     *
     * @return Description of this task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the status icon of this task.
     *
     * @return "X" if this task is done, or a blank space if it is not.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the status icon and description of this task, e.g. {@code "[X] read book"}.
     * Subclasses prepend a type tag (e.g. {@code "[T]"}) and may append extra details.
     *
     * @return String representation of this task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns this task's completion status and description in save-file format,
     * e.g. {@code "1 | read book"}. Subclasses prepend a type tag (e.g. {@code "T"})
     * and may append extra fields, each separated by {@code " | "}.
     *
     * @return Save-file representation of this task.
     */
    public String toSaveFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }
}
