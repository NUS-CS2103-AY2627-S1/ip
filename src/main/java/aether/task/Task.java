package aether.task;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Shared fields and behavior for every task type.
 * Subclasses add a type icon and any date/time details via {@link #toString()}.
 */
public class Task {
    private final String description;
    private TaskStatus status;
    private final TaskType type;

    /**
     * Creates a task that starts as not done.
     *
     * @param description what the user needs to do
     */
    public Task(String description) {
        this(description, TaskType.TODO);
    }

    /**
     * Creates a task of the specified type that starts as not done.
     *
     * @param description what the user needs to do
     * @param type the kind of task
     */
    public Task(String description, TaskType type) {
        this.description = Objects.requireNonNull(description);
        this.status = TaskStatus.PENDING;
        this.type = Objects.requireNonNull(type);
    }

    /**
     * Returns {@code X} if this task is done, or a space if it is not.
     *
     * @return the character shown inside the task's checkbox
     */
    public String getStatusIcon() {
        return (status == TaskStatus.COMPLETED ? "X" : " ");
    }

    /**
     * Records this task as completed.
     */
    public void markAsDone() {
        status = TaskStatus.COMPLETED;
    }

    /**
     * Records this task as not yet completed.
     */
    public void markAsNotDone() {
        status = TaskStatus.PENDING;
    }

    /** Returns whether this task is pending or completed. */
    public TaskStatus getStatus() {
        return status;
    }

    /** Returns the user-provided description of this task. */
    public String getDescription() {
        return description;
    }

    /** Restores this task's status, for example after an unsuccessful save. */
    public void setStatus(TaskStatus status) {
        assert status != null : "A task status must be provided.";
        this.status = status;
    }

    /** Returns the icon identifying this task type. */
    protected String getTypeIcon() {
        return type.getIcon();
    }

    /**
     * Returns the date used by the {@code sort} command, if this task has one.
     * Tasks without a date are placed after dated tasks.
     *
     * @return this task's relevant date, or an empty value for an undated task
     */
    public Optional<LocalDate> getSortDate() {
        return Optional.empty();
    }

    /** Returns whether another task has the same type and description, ignoring description letter case. */
    boolean hasSameDetails(Task other) {
        return type == other.type && description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns the checkbox and description, for example {@code [ ] read book}.
     * Subclasses prepend a type icon and may append date/time details.
     *
     * @return the checkbox and description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
