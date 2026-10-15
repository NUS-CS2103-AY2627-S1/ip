package chudgpt.task;

import java.util.Locale;
import java.util.Objects;

/** Common state and display behavior shared by all supported task types. */
public abstract class Task {
    /** Whether this task is complete. */
    protected boolean isCompleted;

    /** Description of this task. */
    protected final String description;

    private Priority priority = Priority.NONE;

    protected Task(String description) {
        assert description != null : "Task description should not be null";
        this.description = description;
        isCompleted = false;
    }

    /**
     * Changes the completion status of the task.
     *
     * @param isCompleted the target status
     */
    public Task setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
        return this;
    }

    /** Returns the completion status of a task */
    public boolean isCompleted() {
        return isCompleted;
    }

    public Priority getPriority() {
        return priority;
    }

    /**
     * Assigns a priority without changing any other task details.
     *
     * @param priority non-null priority to assign.
     * @return this task.
     */
    public Task setPriority(Priority priority) {
        this.priority = Objects.requireNonNull(priority, "Task priority must not be null");
        return this;
    }

    /**
     * Returns whether another task has the same type and user-entered details.
     * Completion status and priority are intentionally ignored.
     *
     * @param other task to compare with this task.
     * @return {@code true} if both tasks represent the same task details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && normalizeDescription(description).equals(normalizeDescription(other.description));
    }

    /** Returns a normalized description suitable for duplicate comparisons. */
    private static String normalizeDescription(String value) {
        return value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return String.format("[%s]%s %s", isCompleted ? "X" : " ", priority.getDisplayLabel(), description);
    }

    /**
     * Converts a task to its representation in the save file.
     *
     * @return representation of the task in the save file.
     */
    public abstract String toSaveMessage();
}
