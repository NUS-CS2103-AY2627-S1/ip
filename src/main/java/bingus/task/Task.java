package bingus.task;

import java.time.LocalDate;

/**
 * Represents a task with a description that can be marked as complete or incomplete.
 */
public class Task {
    /** Description displayed for this task. */
    private final String description;

    /** Whether this task has been completed. */
    private boolean isDone;
    private final TaskType type;

    /**
     * Creates an incomplete task of the given type.
     *
     * @param description description displayed for this task.
     * @param type type of this task.
     */
    protected Task(String description, TaskType type) {
        assert description != null : "Task description must not be null";
        assert type != null : "Task type must not be null";

        this.description = description;
        this.type = type;
        isDone = false;
    }

    /**
     * Returns this task in the format used for display.
     *
     * @return formatted task text.
     */
    public String getDisplayString() {
        return "[" + type.getSymbol() + "]" + "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns the symbol representing this task's completion status.
     *
     * @return {@code "✓"} if complete, or {@code "○"} if incomplete.
     */
    public String getStatusIcon() {
        return (isDone ? "✓" : "○");
    }

    /**
     * Returns the description stored for this task.
     *
     * @return task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task's type.
     *
     * @return task type.
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns whether this task is complete.
     *
     * @return {@code true} if the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as complete.
     */
    public void mark() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns whether this task is associated with a date.
     *
     * @param date date to check.
     * @return {@code true} if this task is associated with the date.
     */
    public boolean matchesDate(LocalDate date) {
        return false;
    }
}
