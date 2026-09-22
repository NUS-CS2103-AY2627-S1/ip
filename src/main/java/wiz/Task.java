package wiz;

import java.util.Objects;

/**
 * Represents an abstract task with a description and completion status.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a Task with the given description.
     *
     * @param description The task description.
     */
    public Task(String description) {
        assert description != null && !description.isBlank() : "Task description cannot be null or blank";
        this.description = description.trim();
        this.isDone = false;
    }

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
        assert this.isDone : "Task status should be marked as done";
    }

    /**
     * Marks the task as not completed yet.
     */
    public void markAsNotDone() {
        this.isDone = false;
        assert !this.isDone : "Task status should be marked as not done";
    }

    /**
     * Returns the description of the task.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task is completed.
     *
     * @return True if completed, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the status icon indicating completion state.
     *
     * @return "X" if done, or a single space if not done.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Task task = (Task) obj;
        return description.equalsIgnoreCase(task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description.toLowerCase());
    }

    /**
     * Returns the formatted string representation for storage in a data file.
     *
     * @return Encoded task string.
     */
    public abstract String toFileString();
}