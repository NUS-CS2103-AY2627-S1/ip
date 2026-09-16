package bobby;

import java.util.Locale;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /** Text describing the task. */
    private final String description;
    /** Whether the task has been marked complete. */
    private boolean isDone;
    /** Priority assigned to the task. */
    private Priority priority;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.priority = Priority.NONE;
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the symbol used to display this task's completion state.
     *
     * @return {@code "X"} if the task is done, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task's completion state in the storage format.
     *
     * @return {@code "1"} if the task is done, otherwise {@code "0"}
     */
    public String getStorageStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns whether this task is complete.
     *
     * @return {@code true} when the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the priority assigned to this task.
     *
     * @return this task's priority
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Assigns a priority to this task.
     *
     * @param priority priority to assign
     */
    public void setPriority(Priority priority) {
        assert priority != null : "Task priority must not be null";
        this.priority = priority;
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Returns whether another task has the same type and normalized description.
     * Completion state and priority are intentionally not part of a task's identity.
     *
     * @param other task to compare
     * @return {@code true} when both tasks represent the same user-facing details
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && normalizeDescription(description).equals(normalizeDescription(other.description));
    }

    /** Returns the canonical form used to compare task descriptions. */
    private String normalizeDescription(String input) {
        return input.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Returns this task in the format used when saving tasks to disk.
     *
     * @return the line representing this task in storage
     */
    public String toStorageString() {
        return "T | " + getStorageStatus() + " | " + description + " | " + priority;
    }

    /**
     * Returns this task's completion marker and description.
     * Subclasses extend this representation with their task type and details.
     *
     * @return the task display text
     */
    @Override
    public String toString() {
        String priorityBadge = priority == Priority.NONE ? "" : "[P: " + priority + "]";
        return "[" + getStatusIcon() + "]" + priorityBadge + " " + description;
    }
}
