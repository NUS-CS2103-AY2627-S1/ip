package zen.task;

import java.time.LocalDate;

/** Represents a task that may be marked complete and saved to storage. */
public abstract class Task {
    protected String description;
    protected boolean isDone;
    protected final Priority priority;

    /**
     * Creates an incomplete task with the supplied description.
     *
     * @param description the task description.
     */
    public Task(String description) {
        this(description, Priority.NONE);
    }

    /**
     * Creates an incomplete task with the supplied description and priority.
     *
     * @param description the task description
     * @param priority the task priority
     */
    public Task(String description, Priority priority) {
        this.description = description;
        this.isDone = false;
        this.priority = priority;
    }

    /** Returns the icon representing this task's completion status. */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns whether this task's description contains the specified keyword.
     *
     * @param keyword the case-sensitive keyword to search for
     * @return true if the description contains the keyword, false otherwise
     */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword);
    }

    /** Returns this task's priority. */
    public Priority getPriority() {
        return priority;
    }

    /** Returns this task in the format used for persistent storage. */
    public abstract String toStorageString();

    /**
     * Returns whether this task occurs on a specified date.
     *
     * @param date the date to check.
     * @return true if this task occurs on {@code date}.
     */
    public abstract boolean occursOn(LocalDate date);

    @Override
    public String toString() {
        return formatWithPriority(String.format("[%s] %s", this.getStatusIcon(), this.description));
    }

    /** Adds this task's priority to a formatted task description. */
    protected String formatWithPriority(String taskDetails) {
        return String.format("%s (priority: %s)", taskDetails, priority.getDisplayValue());
    }
}
