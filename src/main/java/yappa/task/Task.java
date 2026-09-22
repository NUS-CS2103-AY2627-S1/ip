package yappa.task;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a generic task managed by Yappa.
 * Base class for specific task types like Todo, Deadline, and Event.
 */
public abstract class Task {

    /** Description shown to the user. */
    private final String description;

    /** Whether this task has been completed. */
    private boolean isDone;

    /**
     * Creates a new task with the given description.
     *
     * @param description Task description.
     */
    public Task(String description) {
        this(description, false);
    }

    /**
     * Creates a task with the given description and completion state.
     *
     * @param description Task description.
     * @param isDone      Whether the task is completed.
     */
    public Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Returns the task description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as incomplete.
     */
    public void markAsUndone() {
        this.isDone = false;
    }

    private String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return True if the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's associated date-time, if it has one.
     *
     * @return Task date-time, or an empty value when the task has no date-time.
     */
    public Optional<LocalDateTime> getDateTime() {
        return Optional.empty();
    }

    /**
     * Formats the task's completion state and description for display.
     *
     * @return Display representation of the task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + this.description;
    }

    /**
     * Formats the task as a record suitable for persistent storage.
     *
     * @return Storage representation of the task.
     */
    public abstract String toFileString();

}
